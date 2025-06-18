package ru.kpfu.codeinium.tasko.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TeamPageResponse {
    private String projectName;
    private String invitationCode;
    private List<TeamMemberDto> teamMembers;
} 