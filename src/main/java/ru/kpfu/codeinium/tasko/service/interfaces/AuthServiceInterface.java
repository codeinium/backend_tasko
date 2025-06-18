package ru.kpfu.codeinium.tasko.service.interfaces;

import ru.kpfu.codeinium.tasko.dto.LoginForm;
import ru.kpfu.codeinium.tasko.dto.UserInfoDto;

public interface AuthServiceInterface {
    UserInfoDto authenticateUser(LoginForm loginForm) throws RuntimeException;
} 