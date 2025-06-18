package ru.kpfu.codeinium.tasko.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.kpfu.codeinium.tasko.entity.User;

import java.util.*;

@Getter
public class UserDetailsImpl implements UserDetails {
    private final User user;

    public UserDetailsImpl(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Добавляем как роль пользователя, так и project roles
        Set<GrantedAuthority> authorities = new HashSet<>();

        // Основная роль (ADMIN, USER и т.д.)
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));

        // Project roles (TeamLead, Developer и т.д.)
        user.getProjectRoles().forEach(role ->
                authorities.add(new SimpleGrantedAuthority(role.getName()))
        );

        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

}