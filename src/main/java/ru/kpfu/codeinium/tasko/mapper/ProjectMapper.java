package ru.kpfu.codeinium.tasko.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.kpfu.codeinium.tasko.dto.ProjectDto;
import ru.kpfu.codeinium.tasko.dto.UserDto;
import ru.kpfu.codeinium.tasko.entity.Project;
import ru.kpfu.codeinium.tasko.entity.ProjectMembership;
import ru.kpfu.codeinium.tasko.entity.User;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ProjectMapper {
    @Mapping(target = "teamLeadId", expression = "java(project.getTeamLead().getId())")
    @Mapping(target = "teamMembers", expression = "java(membershipsToUserDtos(project.getMemberships()))")
    ProjectDto toDto(Project project);

    @Mapping(target = "teamLead", ignore = true) // Игнорируем при конвертации в entity
    Project toEntity(ProjectDto projectDto);

    UserDto toUserDto(User user);

    default List<UserDto> membershipsToUserDtos(Set<ProjectMembership> memberships) {
        if (memberships == null) {
            return null;
        }
        return memberships.stream()
                .map(ProjectMembership::getUser)
                .map(this::toUserDto)
                .collect(Collectors.toList());
    }
}
