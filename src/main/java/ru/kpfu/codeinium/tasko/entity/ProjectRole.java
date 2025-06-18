package ru.kpfu.codeinium.tasko.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "project_roles")
@Data
public class ProjectRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name; // Например: "Project Manager", "Developer"

    private String description;
}
