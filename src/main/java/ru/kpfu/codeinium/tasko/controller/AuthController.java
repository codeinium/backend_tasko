package ru.kpfu.codeinium.tasko.controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.kpfu.codeinium.tasko.dto.*;
import ru.kpfu.codeinium.tasko.entity.User;
import ru.kpfu.codeinium.tasko.jwt.JwtTokenProvider;
import ru.kpfu.codeinium.tasko.repository.UserRepository;
import ru.kpfu.codeinium.tasko.security.UserDetailsImpl;
import ru.kpfu.codeinium.tasko.service.EmailService;
import ru.kpfu.codeinium.tasko.service.RegistrationService;
import ru.kpfu.codeinium.tasko.service.ProjectService;

import java.io.IOException;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final RegistrationService registrationService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final ProjectService projectService;

    @PostMapping("/register")
    public ResponseEntity<RegistrationForm> registerUser(
            @RequestBody @Valid RegistrationForm request
    ) {
        RegistrationForm form = registrationService.register(request);
        return new ResponseEntity<>(form, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> loginUser(
            @RequestBody @Valid LoginForm request
    ) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtTokenProvider.generateToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userDetails.getUser();
        boolean isTeamLead = user.getProjectRoles().stream()
                .anyMatch(role -> "TeamLead".equals(role.getName()));

        JwtAuthResponse response = new JwtAuthResponse(
                jwt,
                user.getId(),
                user.getEmail(),
                user.getName(),
                isTeamLead,
                user.getRole().name()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/current-user")
    public ResponseEntity<UserInfoDto> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userDetails.getUser();

        boolean isTeamLead = user.getProjectRoles().stream()
                .anyMatch(role -> "TeamLead".equals(role.getName()));

        UserInfoDto response = new UserInfoDto(
                user.getId(),
                user.getEmail(),
                user.getName(),
                isTeamLead,
                user.getRole().name(),
                user.isEmailVerified()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/team-lead-project")
    public ResponseEntity<Long> getTeamLeadProjectId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long currentUserId = userDetails.getUser().getId();

        return projectService.getProjectIdByTeamLeadId(currentUserId)
                .map(projectId -> new ResponseEntity<>(projectId, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/send-verification")
    public ResponseEntity<?> sendVerificationCode(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userDetails.getUser();

        emailService.sendVerificationEmail(user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(
            @RequestBody VerifyEmailRequest request,
            Authentication authentication
    ) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userDetails.getUser();

        if (user.isEmailVerified()) {
            return ResponseEntity.badRequest().body("Email уже подтвержден");
        }

        if (request.getCode().equals(user.getVerificationCode()) &&
                LocalDateTime.now().isBefore(user.getVerificationCodeExpiry())) {

            user.setEmailVerified(true);
            userRepository.save(user);
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Неверный код или срок действия истек");
    }

    @GetMapping("/verify-email")
    public void verifyEmailByLink(
            @RequestParam String code,
            HttpServletResponse response
    ) throws IOException {
        User user = userRepository.findByVerificationCode(code)
                .orElseThrow(() -> new EntityNotFoundException("Неверный код подтверждения"));

        if (LocalDateTime.now().isBefore(user.getVerificationCodeExpiry())) {
            user.setEmailVerified(true);
            userRepository.save(user);
            response.sendRedirect("/profile?emailVerified=true");
        } else {
            response.sendRedirect("/profile?error=code_expired");
        }
    }
}
