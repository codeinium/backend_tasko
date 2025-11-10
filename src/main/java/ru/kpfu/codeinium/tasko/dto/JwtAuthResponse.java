package ru.kpfu.codeinium.tasko.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtAuthResponse {
    private String token;
    private Long id;
    private String email;
    private String name;
    private boolean teamLead;
    private String role;
}
