package nocomment.orato.global.exception;

import nocomment.orato.domain.analysis.dto.Status;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.MissingServletRequestPartException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public org.springframework.http.ResponseEntity<Status> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::formatFieldError)
                .collect(Collectors.joining(", "));

        return org.springframework.http.ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new Status(400, message));
    }

    @ExceptionHandler({
            MissingServletRequestPartException.class,
            MissingServletRequestParameterException.class,
            HttpMessageNotReadableException.class,
            IllegalArgumentException.class
    })
    public org.springframework.http.ResponseEntity<Status> handleBadRequest(Exception e) {
        String message = e.getMessage() == null || e.getMessage().isBlank()
                ? "잘못된 요청입니다."
                : e.getMessage();

        return org.springframework.http.ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new Status(400, message));
    }

    private String formatFieldError(FieldError error) {
        if (error.getDefaultMessage() == null || error.getDefaultMessage().isBlank()) {
            return error.getField() + " 값이 올바르지 않습니다.";
        }
        return error.getDefaultMessage();
    }
}
