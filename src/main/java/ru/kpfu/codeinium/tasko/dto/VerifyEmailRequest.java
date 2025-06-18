package ru.kpfu.codeinium.tasko.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VerifyEmailRequest {
    @NotNull
    private String code;
}
