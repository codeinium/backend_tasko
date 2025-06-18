package ru.kpfu.codeinium.tasko.dto;

import lombok.*;
import ru.kpfu.codeinium.tasko.constants.UserRole;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;
    private String email;
    private String name;
    private Long teamLeadId;
    private String photoPath;

}