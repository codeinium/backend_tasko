package ru.kpfu.codeinium.tasko.service.interfaces;

import ru.kpfu.codeinium.tasko.dto.RoleDto;

import java.util.List;

public interface RoleServiceInterface {
    List<RoleDto> getAllRoles();
    List<RoleDto> getUserRoles(Long userId);
    void assignRoleToUser(Long userId, Long roleId);
    void removeRoleFromUser(Long userId, Long roleId);
    RoleDto createRole(RoleDto roleDto);
    RoleDto getRoleById(Long roleId);
} 