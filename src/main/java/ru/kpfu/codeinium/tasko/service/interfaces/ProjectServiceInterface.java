package ru.kpfu.codeinium.tasko.service.interfaces;

import ru.kpfu.codeinium.tasko.dto.ProjectDto;
import ru.kpfu.codeinium.tasko.dto.TeamPageResponse;

import java.util.List;
import java.util.Optional;

public interface ProjectServiceInterface {
    ProjectDto createProject(ProjectDto projectDto);
    ProjectDto getProject(Long id);
    List<ProjectDto> getAllProjects();
    TeamPageResponse getTeamPageResponse(Long projectId);
    ProjectDto joinTeam(String invitationCode, Long userId, String role);
    Optional<Long> getProjectIdByTeamLeadId(Long teamLeadId);
    List<ProjectDto> getProjectsByUserId(Long userId);
} 