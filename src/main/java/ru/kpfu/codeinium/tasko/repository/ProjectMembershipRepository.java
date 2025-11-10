package ru.kpfu.codeinium.tasko.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.kpfu.codeinium.tasko.entity.Project;
import ru.kpfu.codeinium.tasko.entity.ProjectMembership;
import ru.kpfu.codeinium.tasko.entity.User;

import java.util.List;
import java.util.Optional;

public interface ProjectMembershipRepository extends JpaRepository<ProjectMembership, Long> {
    boolean existsByUserAndProject(User user, Project project);

    Optional<ProjectMembership> findByUserAndProject(User user, Project project);

    @Query("SELECT pm FROM ProjectMembership pm JOIN FETCH pm.user WHERE pm.project.id = :projectId")
    List<ProjectMembership> findByProjectIdWithUser(@Param("projectId") Long projectId);

    List<ProjectMembership> findByUser_Id(Long userId);

    @Query("SELECT pm FROM ProjectMembership pm JOIN FETCH pm.project p JOIN FETCH p.memberships m JOIN FETCH m.user WHERE pm.user.id = :userId")
    List<ProjectMembership> findByUser_IdWithProjectAndMemberships(@Param("userId") Long userId);
}
