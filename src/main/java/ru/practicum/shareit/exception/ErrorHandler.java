package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleUserValidationException(UserValidationException e) {
        log.error(e.getMessage());
        return Map.of("Validation for user failed", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error(e.getMessage());
        return Map.of("Validation failed", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleUserNotFoundException(UserNotFoundException e) {
        log.error(e.getMessage());
        return Map.of("Search for user failed", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleItemNotFoundException(ItemNotFoundException e) {
        log.error(e.getMessage());
        return Map.of("Search for item failed", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, String> handleNotOwnerForbiddenException(NotOwnerForbiddenException e) {
        log.error(e.getMessage());
        return Map.of("User must be the owner", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleEmailConflictException(EmailConflictException e) {
        log.error(e.getMessage());
        return Map.of("Email conflict has occurred", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleUnknownException(Throwable e) {
        log.error(e.getMessage());
        return Map.of("Unknown error has occurred", e.getMessage());
    }
}
