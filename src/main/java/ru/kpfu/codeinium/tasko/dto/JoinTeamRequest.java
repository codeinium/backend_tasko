package ru.kpfu.codeinium.tasko.dto;

import lombok.Data;

@Data
public class JoinTeamRequest {
    private String invitationCode;
    private String role;
} 