package ru.kpfu.codeinium.tasko.dto;

import lombok.Data;
import ru.kpfu.codeinium.tasko.constants.TaskStatus;

@Data
public class TaskDto {
    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private Long authorId;
    private Long assigneeId;
    private Long projectId;
    private Long boardId;
    private String createdAt;
}

