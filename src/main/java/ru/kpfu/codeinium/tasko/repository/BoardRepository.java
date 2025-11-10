package ru.kpfu.codeinium.tasko.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kpfu.codeinium.tasko.entity.Board;

import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {
    Optional<Board> findBoardById(Long boardId);
    Optional<Board> findByProjectId(Long projectId);
}
