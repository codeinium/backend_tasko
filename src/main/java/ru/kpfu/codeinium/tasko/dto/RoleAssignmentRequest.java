package ru.kpfu.codeinium.tasko.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleAssignmentRequest {
    private Long userId;

    private Long roleId;
}
