package ru.kpfu.codeinium.tasko.service;

import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kpfu.codeinium.tasko.dto.ProjectDto;
import ru.kpfu.codeinium.tasko.dto.TeamMemberDto;
import ru.kpfu.codeinium.tasko.dto.TeamPageResponse;
import ru.kpfu.codeinium.tasko.entity.Project;
import ru.kpfu.codeinium.tasko.entity.ProjectMembership;
import ru.kpfu.codeinium.tasko.entity.User;
import ru.kpfu.codeinium.tasko.exception.NotFoundException;
import ru.kpfu.codeinium.tasko.mapper.ProjectMapper;
import ru.kpfu.codeinium.tasko.repository.ProjectMembershipRepository;
import ru.kpfu.codeinium.tasko.repository.ProjectRepository;
import ru.kpfu.codeinium.tasko.repository.UserRepository;
import ru.kpfu.codeinium.tasko.service.interfaces.ProjectServiceInterface;

import java.util.List;
import java.util.Set;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService implements ProjectServiceInterface {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectMapper projectMapper;
    private final ProjectMembershipRepository projectMembershipRepository;
    private final EmailService emailService;

    @Override
    @Transactional
    public ProjectDto createProject(ProjectDto projectDto) {
        Project project = projectMapper.toEntity(projectDto);

        // Устанавливаем teamLead из базы данных
        User teamLead = userRepository.findById(projectDto.getTeamLeadId())
                .orElseThrow(() -> new NotFoundException("User not found"));
        project.setTeamLead(teamLead);

        Project savedProject = projectRepository.save(project);

        // Добавляем teamLead как участника проекта с ролью "TeamLead"
        ProjectMembership teamLeadMembership = new ProjectMembership();
        teamLeadMembership.setProject(savedProject);
        teamLeadMembership.setUser(teamLead);
        teamLeadMembership.setRole("TeamLead"); // Устанавливаем роль
        projectMembershipRepository.save(teamLeadMembership);

        // Добавляем созданное членство в коллекцию memberships проекта
        savedProject.getMemberships().add(teamLeadMembership);

        return projectMapper.toDto(savedProject);
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectDto getProject(Long id) {
        Project project = projectRepository.findByIdWithMemberships(id)
                .orElseThrow(() -> new NotFoundException("Project not found"));

        return projectMapper.toDto(project);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectDto> getAllProjects() {
        List<Project> projects = projectRepository.findAllWithMemberships();

        return projects.stream()
                .map(projectMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TeamPageResponse getTeamPageResponse(Long projectId) {
        Project project  = projectRepository.findByIdWithMemberships(projectId)
                .orElseThrow(() -> new NotFoundException("Project not found"));

        System.out.println("Project memberships size: " + project.getMemberships().size());

        List<TeamMemberDto> teamMembers = project.getMemberships().stream()
                .map(membership -> TeamMemberDto.builder()
                        .id(membership.getUser().getId())
                        .name(membership.getUser().getName())
                        .email(membership.getUser().getEmail())
                        .photoPath(membership.getUser().getPhotoPath())
                        .role(membership.getRole())
                        .build())
                .collect(Collectors.toList());

        return TeamPageResponse.builder()
                .projectName(project.getName())
                .invitationCode(project.getInvitationCode())
                .teamMembers(teamMembers)
                .build();
    }

    @Override
    @Transactional
    public ProjectDto joinTeam(String invitationCode, Long userId, String role) {
        Project project = projectRepository.findByInvitationCode(invitationCode)
                .orElseThrow(() -> new NotFoundException("Project not found with this invitation code"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        // Проверить, не является ли пользователь уже членом этой команды, используя репозиторий
        boolean alreadyMember = projectMembershipRepository.existsByUserAndProject(user, project);

        if (alreadyMember) {
            throw new IllegalArgumentException("User is already a member of this project");
        }

        ProjectMembership membership = new ProjectMembership();
        membership.setProject(project);
        membership.setUser(user);
        membership.setRole(role); // Используем переданную роль

        projectMembershipRepository.save(membership);
        emailService.sendProjectJoinNotificationEmail(user, project);

        // Добавляем созданное членство в коллекцию memberships проекта
        project.getMemberships().add(membership);

        return projectMapper.toDto(project);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Long> getProjectIdByTeamLeadId(Long teamLeadId) {
        return projectRepository.findByTeamLeadId(teamLeadId)
                .map(Project::getId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectDto> getProjectsByUserId(Long userId) {
        List<ProjectMembership> memberships = projectMembershipRepository.findByUser_IdWithProjectAndMemberships(userId);
        return memberships.stream()
                .map(ProjectMembership::getProject)
                .map(projectMapper::toDto)
                .collect(Collectors.toList());
    }
}