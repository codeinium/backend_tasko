package ru.kpfu.codeinium.tasko.mapper;

import org.mapstruct.Mapper;
import ru.kpfu.codeinium.tasko.dto.UserDto;
import ru.kpfu.codeinium.tasko.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    default UserDto toDto(User user) {
        if (user == null) {
            return null;
        }

        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setPhotoPath(user.getPhotoPath()); // Добавьте эту строку

        return dto;
    }

    UserDto mapUserToUserDto(User user);
}