package ru.kpfu.codeinium.tasko.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kpfu.codeinium.tasko.dto.RoleDto;
import ru.kpfu.codeinium.tasko.entity.ProjectRole;
import ru.kpfu.codeinium.tasko.entity.User;
import ru.kpfu.codeinium.tasko.repository.RoleRepository;
import ru.kpfu.codeinium.tasko.repository.UserRepository;
import ru.kpfu.codeinium.tasko.service.interfaces.RoleServiceInterface;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService implements RoleServiceInterface {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    @Override
    public List<RoleDto> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(RoleDto::fromEntity)
                .toList();
    }

    @Override
    public List<RoleDto> getUserRoles(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        return user.getProjectRoles().stream()
                .map(RoleDto::fromEntity)
                .toList();
    }

    @Override
    @Transactional
    public void assignRoleToUser(Long userId, Long roleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        ProjectRole projectRole = roleRepository.findById(roleId)
                .orElseThrow(() -> new EntityNotFoundException("Role not found"));

        if (!user.getProjectRoles().contains(projectRole)) {
            user.getProjectRoles().add(projectRole);
            userRepository.save(user);
        }
    }

    @Override
    @Transactional
    public void removeRoleFromUser(Long userId, Long roleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        ProjectRole projectRole = roleRepository.findById(roleId)
                .orElseThrow(() -> new EntityNotFoundException("Role not found"));

        user.getProjectRoles().remove(projectRole);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public RoleDto createRole(RoleDto roleDto) {
        ProjectRole projectRole = new ProjectRole();
        projectRole.setName(roleDto.getName());
        projectRole.setDescription(roleDto.getDescription());

        return RoleDto.fromEntity(roleRepository.save(projectRole));
    }

    @Override
    public RoleDto getRoleById(Long roleId) {
        return roleRepository.findById(roleId)
                .map(RoleDto::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException("Role not found"));
    }
}
