package ru.kpfu.codeinium.tasko.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationForm {

    @Email(message = "Пожалуйста, введите корректный email")
    @NotEmpty(message = "Поле email не может быть пустым")
    private String email;

    @Size(min = 1, max = 50, message = "Имя должно содержать от 1 до 50 символов")
    @NotEmpty(message = "Поле имя не может быть пустым")
    private String name;

    @NotEmpty(message = "Поле пароль не может быть пустым")
    @Size(min = 8)
    private String password;

    private boolean isTeamLead;

    private Long userId;

    private String role;

    private Long projectId;



}
