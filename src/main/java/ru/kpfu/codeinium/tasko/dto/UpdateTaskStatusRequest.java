package ru.kpfu.codeinium.tasko.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.kpfu.codeinium.tasko.constants.TaskStatus;

@Data
public class UpdateTaskStatusRequest {
    @NotNull
    private TaskStatus status;
}
