package ru.kpfu.codeinium.tasko.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class ProjectCreateDto {
    @NotBlank(message = "Project name cannot be blank")
    private String name;

    private String description;
}
