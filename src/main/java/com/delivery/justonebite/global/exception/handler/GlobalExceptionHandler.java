package com.delivery.justonebite.global.exception.handler;

import com.delivery.justonebite.global.exception.custom.CustomException;
import com.delivery.justonebite.global.exception.response.ErrorResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import static com.delivery.justonebite.global.exception.response.ErrorCode.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 도메인 예외 */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        return ResponseEntity.status(e.getStatus())
                .body(new ErrorResponse(e.getErrorCode()));
    }

    /** @Valid 바디 검증 실패 */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        String description = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .findFirst().orElse(INVALID_INPUT_DATA.getDescription());
        return ResponseEntity.status(BAD_REQUEST)
                .body(new ErrorResponse(INVALID_INPUT_DATA, description));
    }

    /** 잘못된 요청 형식: 파라미터 누락/타입 불일치/본문 파싱 실패 */
    @ExceptionHandler({
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequest(Exception e) {
        return ResponseEntity.status(INVALID_INPUT_DATA.getStatus())
                .body(new ErrorResponse(INVALID_INPUT_DATA, e.getMessage()));
    }

    /** 미지원 메서드(405) */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotAllowed(HttpRequestMethodNotSupportedException e) {
        return ResponseEntity.status(METHOD_NOT_ALLOWED.getStatus())
                .body(new ErrorResponse(METHOD_NOT_ALLOWED, e.getMessage()));
    }

    /** DB 무결성/중복키(409) */
    @ExceptionHandler({DataIntegrityViolationException.class, DuplicateKeyException.class})
    public ResponseEntity<ErrorResponse> handleDataIntegrity(Exception e) {
        return ResponseEntity.status(DATA_INTEGRITY_VIOLATION.getStatus())
                .body(new ErrorResponse(DATA_INTEGRITY_VIOLATION, "데이터 무결성에 위배되었습니다."));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e) {
        return ResponseEntity.status(FORBIDDEN_ACCESS.getStatus()).body(new ErrorResponse(FORBIDDEN_ACCESS, e.getMessage()));
    }

    /** 그 밖의 예외(500) */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(INTERNAL_SERVER_ERROR.getStatus())
                .body(new ErrorResponse(INTERNAL_SERVER_ERROR));
    }

}
