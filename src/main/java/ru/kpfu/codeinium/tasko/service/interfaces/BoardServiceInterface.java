package ru.kpfu.codeinium.tasko.service.interfaces;

import ru.kpfu.codeinium.tasko.dto.BoardDto;

import java.util.List;

public interface BoardServiceInterface {
    BoardDto createBoard(BoardDto boardDto);
    BoardDto getBoard(Long id);
    List<BoardDto> getAllBoards();
    List<BoardDto> getBoardsByProject(Long projectId);
    BoardDto updateBoard(Long id, BoardDto boardDto);
    void deleteBoard(Long id);
} 