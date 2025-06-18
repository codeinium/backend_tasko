package ru.kpfu.codeinium.tasko.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kpfu.codeinium.tasko.entity.ProjectRole;

public interface RoleRepository extends JpaRepository<ProjectRole, Long> {

}
