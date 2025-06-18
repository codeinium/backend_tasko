package ru.kpfu.codeinium.tasko.service;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import ru.kpfu.codeinium.tasko.entity.User;
import ru.kpfu.codeinium.tasko.entity.Project;
import ru.kpfu.codeinium.tasko.service.interfaces.EmailServiceInterface;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailService implements EmailServiceInterface {

    private final JavaMailSender mailSender;

    @Value("${app.base-url}")
    private String baseUrl;

    @Override
    public void sendVerificationEmail(User user) {
        String verificationCode = generateVerificationCode();
        user.setVerificationCode(verificationCode);
        user.setVerificationCodeExpiry(LocalDateTime.now().plusHours(24));

        String subject = "Подтверждение email";
        String content = "Ваш код подтверждения: " + verificationCode +
                "\nИли перейдите по ссылке: " +
                baseUrl + "/api/auth/verify-email?code=" + verificationCode;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject(subject);
        message.setText(content);

        mailSender.send(message);
    }

    @Override
    public void sendProjectJoinNotificationEmail(User user, Project project) {
        String subject = "Вы присоединились к проекту в Tasko!";
        String content = String.format("Поздравляем, %s!\n\nВы успешно присоединились к проекту \"%s\".\n\nНачните работу над своими задачами прямо сейчас!",
                user.getName() != null ? user.getName() : user.getEmail(),
                project.getName());

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject(subject);
        message.setText(content);

        mailSender.send(message);
    }

    private String generateVerificationCode() {
        return String.valueOf(100000 + new Random().nextInt(900000));
    }
}
