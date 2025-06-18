package ru.kpfu.codeinium.tasko.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.kpfu.codeinium.tasko.entity.Project;
import ru.kpfu.codeinium.tasko.entity.User;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    Optional<Project> findByInvitationCode(String invitationCode);

    @Query("SELECT p FROM Project p JOIN FETCH p.memberships m JOIN FETCH m.user WHERE p.id = :id")
    Optional<Project> findByIdWithMemberships(@Param("id") Long id);

    @Query("SELECT p FROM Project p JOIN FETCH p.memberships m JOIN FETCH m.user")
    List<Project> findAllWithMemberships();

    @Query("SELECT p FROM Project p JOIN p.memberships m WHERE m.user = :user")
    List<Project> findByUser(@Param("user") User user);

    Optional<Project> findByTeamLeadId(Long teamLeadId);
}
