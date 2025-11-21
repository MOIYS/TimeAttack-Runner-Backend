package MOIYS.project.TimeAttack_Runner_Backend.exception;

import java.util.NoSuchElementException;

import jakarta.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> toBadRequest() {
        return ResponseEntity
                .badRequest()
                .body(ErrorResponse.of("BAD_REQUEST", "올바르지 않은 요청입니다."));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> toNotFound(NoSuchElementException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of("NOT_FOUND", "요청한 리소스를 찾을 수 없습니다."));
    }
}
