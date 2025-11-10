package ru.kpfu.codeinium.tasko.dto;

import lombok.Data;
import ru.kpfu.codeinium.tasko.entity.ProjectRole;

@Data
public class RoleDto {
    private Long id;
    private String name;
    private String description;

    public static RoleDto fromEntity(ProjectRole projectRole) {
        RoleDto dto = new RoleDto();
        dto.setId(projectRole.getId());
        dto.setName(projectRole.getName());
        dto.setDescription(projectRole.getDescription());
        return dto;
    }
}
