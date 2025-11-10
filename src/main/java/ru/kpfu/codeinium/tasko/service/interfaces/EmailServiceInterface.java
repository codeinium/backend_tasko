package ru.kpfu.codeinium.tasko.service.interfaces;

import ru.kpfu.codeinium.tasko.entity.Project;
import ru.kpfu.codeinium.tasko.entity.User;

public interface EmailServiceInterface {
    void sendVerificationEmail(User user);
    void sendProjectJoinNotificationEmail(User user, Project project);
} 