package ru.kpfu.codeinium.tasko.dto;

import lombok.Data;

@Data
public class BoardDto {
    private Long id;
    private String name;
    private Long projectId;
}