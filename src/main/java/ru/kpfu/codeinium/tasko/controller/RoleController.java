package ru.kpfu.codeinium.tasko.controller;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.kpfu.codeinium.tasko.dto.RoleAssignmentRequest;
import ru.kpfu.codeinium.tasko.dto.RoleDto;
import ru.kpfu.codeinium.tasko.service.RoleService;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RoleDto>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoleDto> getRoleById(@PathVariable Long id) {
        return ResponseEntity.ok(roleService.getRoleById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoleDto> createRole(
            @RequestBody @Validated RoleDto roleDto
    ) {
        return new ResponseEntity<>(
                roleService.createRole(roleDto),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == principal.id")
    public ResponseEntity<List<RoleDto>> getUserRoles(
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(roleService.getUserRoles(userId));
    }

    @PostMapping("/assign")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> assignRoleToUser(
            @RequestBody @Validated RoleAssignmentRequest request
    ) {
        roleService.assignRoleToUser(request.getUserId(), request.getRoleId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/revoke")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> removeRoleFromUser(
            @RequestBody @Validated RoleAssignmentRequest request
    ) {
        roleService.removeRoleFromUser(request.getUserId(), request.getRoleId());
        return ResponseEntity.noContent().build();
    }

}