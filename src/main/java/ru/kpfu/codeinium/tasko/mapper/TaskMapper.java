package ru.kpfu.codeinium.tasko.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.kpfu.codeinium.tasko.dto.TaskDto;
import ru.kpfu.codeinium.tasko.entity.Task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "board.id", target = "boardId")
    @Mapping(source = "assignee.id", target = "assigneeId")
    @Mapping(source = "createdAt", target = "createdAt", qualifiedByName = "formatDateTime")
    TaskDto toDto(Task task);

    @Mapping(target = "author", ignore = true)
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "board", ignore = true)
    @Mapping(target = "assignee", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Task toEntity(TaskDto taskDto);

    @Named("formatDateTime")
    default String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        return dateTime.format(formatter);
    }
}