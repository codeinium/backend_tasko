package ru.kpfu.codeinium.tasko.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Data
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;

    @Column(unique = true, nullable = false)
    private String invitationCode;

    @ManyToOne
    @JoinColumn(name = "team_lead_id", nullable = false)
    private User teamLead;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    private Set<ProjectMembership> memberships = new HashSet<>();

    @OneToMany(mappedBy = "project")
    private List<Board> boards = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        invitationCode = generateInvitationCode();
    }

    private String generateInvitationCode() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
