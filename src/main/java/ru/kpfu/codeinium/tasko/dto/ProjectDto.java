package ru.kpfu.codeinium.tasko.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProjectDto {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private Long teamLeadId;
    private List<UserDto> teamMembers;
}
