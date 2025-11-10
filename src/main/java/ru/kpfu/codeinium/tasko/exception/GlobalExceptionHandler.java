package ru.kpfu.codeinium.tasko.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger userLogger = LoggerFactory.getLogger("ru.kpfu.codeinium.tasko.user");
    private static final Logger coreLogger = LoggerFactory.getLogger("ru.kpfu.codeinium.tasko");
    private static final Logger taskLogger = LoggerFactory.getLogger("ru.kpfu.codeinium.tasko.task");

    @ExceptionHandler(UserException.class)
    public ResponseEntity<String> handleUserException(UserException ex) {
        userLogger.error("User-related error: {}", ex.getMessage(), ex);
        return new ResponseEntity<>("User error: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TaskException.class)
    public ResponseEntity<String> handleTaskException(TaskException ex) {
        taskLogger.error("Task-related error: {}", ex.getMessage(), ex);
        return new ResponseEntity<>("Task error: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAllExceptions(Exception ex) {
        coreLogger.error("An unexpected error occurred: {}", ex.getMessage(), ex);
        return new ResponseEntity<>("An unexpected error occurred: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
} 