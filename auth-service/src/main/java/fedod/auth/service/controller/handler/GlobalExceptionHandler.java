package fedod.auth.service.controller.handler;

import fedod.auth.service.dto.response.ErrorResponse;
import fedod.auth.service.exception.InvalidCredentialsException;
import fedod.auth.service.exception.InvalidRefreshTokenException;
import fedod.auth.service.exception.UserAlreadyExistsException;
import fedod.auth.service.exception.UserBlockedException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExists(UserAlreadyExistsException ex, HttpServletRequest request) {
        log.warn("User already exists: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .error("User already exists")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .details(List.of(ex.getClass().getName()))
                .build();

        return ResponseEntity.status(errorResponse.status()).body(errorResponse);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException ex, HttpServletRequest request) {
        log.warn("Invalid credentials: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error("Invalid credentials")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .details(List.of(ex.getClass().getName()))
                .build();

        return ResponseEntity.status(errorResponse.status()).body(errorResponse);
    }

    @ExceptionHandler(UserBlockedException.class)
    public ResponseEntity<ErrorResponse> handleUserBlocked(UserBlockedException ex, HttpServletRequest request) {
        log.warn("User blocked: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.FORBIDDEN.value())
                .error("User blocked")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .details(List.of(ex.getClass().getName()))
                .build();

        return ResponseEntity.status(errorResponse.status()).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        log.warn("Validation failed: {}", ex.getMessage());

        List<String> validationErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Validation Failed")
                .message("Input validation failed")
                .path(request.getRequestURI())
                .details(validationErrors)
                .build();

        return ResponseEntity.status(errorResponse.status()).body(errorResponse);
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public org.springframework.http.ResponseEntity<ErrorResponse> handleInvalidRefreshToken(
            InvalidRefreshTokenException ex,
            HttpServletRequest request
    ) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.UNAUTHORIZED.value())
                        .error("Invalid Refresh Token")
                        .message(ex.getMessage())
                        .path(request.getRequestURI())
                        .details(List.of(ex.getClass().getName()))
                        .build()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception: ", ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .details(List.of(ex.getClass().getName()))
                .build();

        return ResponseEntity.status(errorResponse.status()).body(errorResponse);
    }
}
