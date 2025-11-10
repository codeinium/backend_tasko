package ru.kpfu.codeinium.tasko.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.kpfu.codeinium.tasko.dto.UserDto;
import ru.kpfu.codeinium.tasko.entity.User;
import ru.kpfu.codeinium.tasko.exception.InvalidFileException;
import ru.kpfu.codeinium.tasko.mapper.UserMapper;
import ru.kpfu.codeinium.tasko.repository.UserRepository;
import ru.kpfu.codeinium.tasko.security.UserDetailsImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    private final Path uploadDir = Paths.get("src/main/resources/static/uploads");

    @PostMapping("/photo")
    public ResponseEntity<?> uploadPhoto(@RequestParam("file") MultipartFile file, Authentication authentication) {
        logger.info("Received request to upload photo for user");
        if (authentication == null || !authentication.isAuthenticated()) {
            logger.error("User not authenticated");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userDetails.getUser();
        logger.info("Uploading photo for user ID: {}", user.getId());
        if (!file.getContentType().startsWith("image/")) {
            logger.error("Invalid file type: {}", file.getContentType());
            throw new InvalidFileException("Недопустимый тип файла");
        }
        if (file.getSize() > 10 * 1024 * 1024) {
            logger.error("File size exceeds limit: {}", file.getSize());
            throw new InvalidFileException("Файл слишком большой, он должен быть < 10МБ");
        }
        String userUploadDir = uploadDir + "/" + user.getId();
        Path dirPath = Paths.get(userUploadDir);
        try {
            Files.createDirectories(dirPath);
            Path filePath = dirPath.resolve("photo.jpg");
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            logger.info("Photo saved to: {}", filePath);
        } catch (IOException e) {
            logger.error("Error saving photo: {}", e.getMessage());
            throw new InvalidFileException("Ошибка сохранения файоа");
        }
        String photoPath = "/uploads/" + user.getId() + "/photo.jpg";
        user.setPhotoPath(photoPath);
        User savedUser = userRepository.save(user);
        UserDto userDto = userMapper.toDto(savedUser);
        logger.info("Photo uploaded successfully for user ID: {}", user.getId());
        return ResponseEntity.ok(userDto);
    }

    @GetMapping("/photo")
    public ResponseEntity<byte[]> getProfilePhoto(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userDetails.getUser();

        if (user.getPhotoPath() == null || user.getPhotoPath().isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        try {
            Path photoPath = Paths.get("src/main/resources/static" + user.getPhotoPath());
            byte[] photoBytes = Files.readAllBytes(photoPath);

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(photoBytes);
        } catch (IOException e) {
            logger.error("Error reading photo: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    public ResponseEntity<UserDto> getProfile(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            logger.error("User not authenticated");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userDetails.getUser();
        UserDto userDto = userMapper.toDto(user);
        return ResponseEntity.ok(userDto);
    }
}
