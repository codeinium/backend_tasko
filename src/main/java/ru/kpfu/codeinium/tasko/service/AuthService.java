package ru.kpfu.codeinium.tasko.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.kpfu.codeinium.tasko.dto.LoginForm;
import ru.kpfu.codeinium.tasko.dto.UserInfoDto;
import ru.kpfu.codeinium.tasko.entity.User;
import ru.kpfu.codeinium.tasko.repository.UserRepository;
import ru.kpfu.codeinium.tasko.service.interfaces.AuthServiceInterface;

@Service
@RequiredArgsConstructor
public class AuthService implements AuthServiceInterface {

    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    @Override
    public UserInfoDto authenticateUser(LoginForm loginForm) throws RuntimeException {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginForm.getEmail(),
                        loginForm.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userRepository.findByEmail(loginForm.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean isTeamLead = user.getProjectRoles().stream()
                .anyMatch(role -> "TeamLead".equals(role.getName()));

        return new UserInfoDto(
                user.getId(),
                user.getEmail(),
                user.getName(),
                isTeamLead,
                user.getRole().name(),
                user.isEmailVerified()
        );
    }
}
