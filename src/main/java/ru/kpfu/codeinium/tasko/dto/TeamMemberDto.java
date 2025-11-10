package ru.kpfu.codeinium.tasko.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TeamMemberDto {
    private Long id;
    private String name;
    private String email;
    private String photoPath;
    private String role;
} 