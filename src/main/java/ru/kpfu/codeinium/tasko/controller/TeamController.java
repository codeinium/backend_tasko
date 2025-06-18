package ru.kpfu.codeinium.tasko.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kpfu.codeinium.tasko.dto.JoinTeamRequest;
import ru.kpfu.codeinium.tasko.dto.ProjectIdRequest;
import ru.kpfu.codeinium.tasko.dto.TeamPageResponse;
import ru.kpfu.codeinium.tasko.service.ProjectService;

import java.util.Optional;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TeamController {

    private final ProjectService projectService;

    @GetMapping("/projects/{projectId}/team")
    public ResponseEntity<TeamPageResponse> getTeamPage(@PathVariable Long projectId) {
        TeamPageResponse response = projectService.getTeamPageResponse(projectId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/team/join")
    public ResponseEntity<Void> joinTeam(@RequestBody JoinTeamRequest request, @RequestHeader("X-User-Id") Long userId) {
        projectService.joinTeam(request.getInvitationCode(), userId, request.getRole());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user/team-lead-project")
    public ResponseEntity<Long> getTeamLeadProjectId(@RequestHeader("X-User-Id") Long userId) {
        Optional<Long> projectId = projectService.getProjectIdByTeamLeadId(userId);
        return projectId.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }
} 