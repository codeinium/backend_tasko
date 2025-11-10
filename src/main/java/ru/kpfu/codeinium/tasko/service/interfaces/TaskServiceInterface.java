package ru.kpfu.codeinium.tasko.service.interfaces;

import ru.kpfu.codeinium.tasko.dto.TaskDto;

import java.util.List;

public interface TaskServiceInterface {
    TaskDto createTask(TaskDto taskDto);
    TaskDto updateTaskAssignee(Long id, Long assigneeId);
    List<TaskDto> getTasksForCurrentUser();
    TaskDto getTaskById(Long id);
    TaskDto updateTask(Long id, TaskDto taskDto);
    void deleteTask(Long id);
    TaskDto updateTaskStatus(Long id, String newStatus);
} 