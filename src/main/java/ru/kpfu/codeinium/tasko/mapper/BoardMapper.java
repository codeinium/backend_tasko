package ru.kpfu.codeinium.tasko.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.kpfu.codeinium.tasko.dto.BoardDto;
import ru.kpfu.codeinium.tasko.entity.Board;

@Mapper(componentModel = "spring")
public interface BoardMapper {
    @Mapping(source = "project.id", target = "projectId")
    BoardDto toDto(Board board);

    @Mapping(target = "project", ignore = true)
    Board toEntity(BoardDto boardDto);
}
