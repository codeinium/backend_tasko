package ru.kpfu.codeinium.tasko.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kpfu.codeinium.tasko.dto.BoardDto;
import ru.kpfu.codeinium.tasko.entity.Board;
import ru.kpfu.codeinium.tasko.entity.Project;
import ru.kpfu.codeinium.tasko.exception.NotFoundException;
import ru.kpfu.codeinium.tasko.mapper.BoardMapper;
import ru.kpfu.codeinium.tasko.repository.BoardRepository;
import ru.kpfu.codeinium.tasko.repository.ProjectRepository;
import ru.kpfu.codeinium.tasko.service.interfaces.BoardServiceInterface;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardService implements BoardServiceInterface {

    private final BoardRepository boardRepository;
    private final ProjectRepository projectRepository;
    private final BoardMapper boardMapper;

    @Override
    @Transactional
    public BoardDto createBoard(BoardDto boardDto) {
        Project project = projectRepository.findById(boardDto.getProjectId())
                .orElseThrow(() -> new NotFoundException("Project not found with id: " + boardDto.getProjectId()));

        Board board = boardMapper.toEntity(boardDto);
        board.setProject(project);

        Board savedBoard = boardRepository.save(board);
        return boardMapper.toDto(savedBoard);
    }

    @Override
    @Transactional(readOnly = true)
    public BoardDto getBoard(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Board not found with id: " + id));
        return boardMapper.toDto(board);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BoardDto> getAllBoards() {
        return boardRepository.findAll().stream()
                .map(boardMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BoardDto> getBoardsByProject(Long projectId) {
        return boardRepository.findByProjectId(projectId).stream()
                .map(boardMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BoardDto updateBoard(Long id, BoardDto boardDto) {
        Board existingBoard = boardRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Board not found with id: " + id));

        existingBoard.setName(boardDto.getName());

        if (!existingBoard.getProject().getId().equals(boardDto.getProjectId())) {
            Project project = projectRepository.findById(boardDto.getProjectId())
                    .orElseThrow(() -> new NotFoundException("Project not found with id: " + boardDto.getProjectId()));
            existingBoard.setProject(project);
        }

        Board updatedBoard = boardRepository.save(existingBoard);
        return boardMapper.toDto(updatedBoard);
    }

    @Override
    @Transactional
    public void deleteBoard(Long id) {
        boardRepository.deleteById(id);
    }
}
