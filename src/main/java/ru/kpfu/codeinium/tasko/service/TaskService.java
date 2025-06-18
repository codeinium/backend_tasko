package ru.kpfu.codeinium.tasko.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kpfu.codeinium.tasko.constants.TaskStatus;
import ru.kpfu.codeinium.tasko.dto.TaskDto;
import ru.kpfu.codeinium.tasko.entity.Project;
import ru.kpfu.codeinium.tasko.entity.Task;
import ru.kpfu.codeinium.tasko.mapper.TaskMapper;
import ru.kpfu.codeinium.tasko.repository.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.AccessDeniedException;
import ru.kpfu.codeinium.tasko.security.UserDetailsImpl;
import ru.kpfu.codeinium.tasko.service.interfaces.TaskServiceInterface;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService implements TaskServiceInterface {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final BoardRepository boardRepository;
    private final TaskMapper taskMapper;
    private final ProjectMembershipRepository projectMembershipRepository;

    @Override
    public TaskDto createTask(TaskDto taskDto) {
        Task task = taskMapper.toEntity(taskDto);

        task.setAuthor(userRepository.findById(taskDto.getAuthorId())
                .orElseThrow(() -> new RuntimeException("Author not found with id: " + taskDto.getAuthorId())));
        task.setProject(projectRepository.findById(taskDto.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + taskDto.getProjectId())));
        task.setBoard(boardRepository.findById(taskDto.getBoardId())
                .orElseThrow(() -> new RuntimeException("Board not found with id: " + taskDto.getBoardId())));
        if (taskDto.getAssigneeId() != null) {
            task.setAssignee(userRepository.findById(taskDto.getAssigneeId())
                    .orElseThrow(() -> new RuntimeException("Assignee not found with id: " + taskDto.getAssigneeId())));
        }

        Task savedTask = taskRepository.save(task);
        return taskMapper.toDto(savedTask);
    }

    @Override
    public TaskDto updateTaskAssignee(Long id, Long assigneeId) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
        task.setAssignee(userRepository.findById(assigneeId)
                .orElseThrow(() -> new RuntimeException("Assignee not found with id: " + assigneeId)));
        Task updatedTask = taskRepository.save(task);
        return taskMapper.toDto(updatedTask);
    }

    @Override
    public List<TaskDto> getTasksForCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long currentUserId = ((UserDetailsImpl) authentication.getPrincipal()).getUser().getId();

        List<Long> projectIds = projectMembershipRepository.findByUser_Id(currentUserId).stream()
                .map(membership -> membership.getProject().getId())
                .collect(Collectors.toList());

        if (projectIds.isEmpty()) {
            return List.of(); // Если пользователь не состоит ни в одном проекте, возвращаем пустой список
        }

        List<Task> tasks = taskRepository.findByProjectIdIn(projectIds);
        return tasks.stream()
                .map(taskMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public TaskDto getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long currentUserId = ((UserDetailsImpl) authentication.getPrincipal()).getUser().getId();

        boolean isMemberOfProject = projectMembershipRepository.findByUser_Id(currentUserId).stream()
                .anyMatch(membership -> membership.getProject().getId().equals(task.getProject().getId()));

        if (!isMemberOfProject) {
            throw new AccessDeniedException("User is not a member of the project this task belongs to");
        }

        return taskMapper.toDto(task);
    }

    @Override
    public TaskDto updateTask(Long id, TaskDto taskDto) {
        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));

        existingTask.setTitle(taskDto.getTitle());
        existingTask.setDescription(taskDto.getDescription());
        existingTask.setStatus(taskDto.getStatus());

        Task updatedTask = taskRepository.save(existingTask);
        return taskMapper.toDto(updatedTask);
    }

    @Override
    public void deleteTask(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long currentUserId = ((UserDetailsImpl) authentication.getPrincipal()).getUser().getId();

        ru.kpfu.codeinium.tasko.entity.User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("Current user not found"));

        boolean isTeamLead = currentUser.getProjectRoles().stream()
                .anyMatch(role -> "TeamLead".equals(role.getName()));

        if (!isTeamLead) {
            throw new AccessDeniedException("Only Team Leads can delete tasks");
        }

        taskRepository.deleteById(id);
    }

    @Override
    public TaskDto updateTaskStatus(Long id, String newStatus) {
        try {
            TaskStatus status = TaskStatus.valueOf(newStatus);
            Task task = taskRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
            task.setStatus(status);
            Task updatedTask = taskRepository.save(task);
            return taskMapper.toDto(updatedTask);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status: " + newStatus);
        }
    }
}
