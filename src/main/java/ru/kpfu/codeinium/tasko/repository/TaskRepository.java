package ru.kpfu.codeinium.tasko.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.kpfu.codeinium.tasko.entity.Task;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByProjectId(Long projectId);

    @Query("SELECT t FROM Task t JOIN t.board b WHERE b.project.id = :projectId")
    List<Task> findAllByProject(@Param("projectId") Long projectId);

    List<Task> findByProjectIdIn(List<Long> projectIds);
}
