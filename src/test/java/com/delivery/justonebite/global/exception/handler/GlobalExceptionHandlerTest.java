package com.delivery.justonebite.global.exception.handler;

import com.delivery.justonebite.global.exception.custom.CustomException;
import com.delivery.justonebite.global.exception.response.ErrorCode;
import com.delivery.justonebite.global.exception.response.ErrorResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("CustomException → 403 + FORBIDDEN_ACCESS")
    void handleCustomException() {
        CustomException ex = new CustomException(ErrorCode.FORBIDDEN_ACCESS);

        var resp = handler.handleCustomException(ex);

        assertEquals(HttpStatus.FORBIDDEN, resp.getStatusCode());
        ErrorResponse body = resp.getBody();
        assertNotNull(body);
        assertEquals(ErrorCode.FORBIDDEN_ACCESS, body.getErrorCode());
        assertEquals(ErrorCode.FORBIDDEN_ACCESS.getDescription(), body.getDescription());
        assertEquals(HttpStatus.FORBIDDEN.value(), body.getStatus());
    }

    @Test
    @DisplayName("@Valid 실패 → 400 + INVALID_INPUT_DATA (첫 번째 필드 에러 메시지 노출)")
    void handleMethodArgumentNotValid() throws NoSuchMethodException {
        Method m = DummyController.class.getMethod("create", DummyDto.class);
        MethodParameter mp = new MethodParameter(m, 0);

        DummyDto target = new DummyDto();
        BeanPropertyBindingResult br = new BeanPropertyBindingResult(target, "dummy");
        br.addError(new FieldError("dummy", "name", "must not be blank"));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(mp, br);


        var resp = handler.handleMethodArgumentNotValid(ex);

        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        ErrorResponse body = resp.getBody();
        assertNotNull(body);
        assertEquals(ErrorCode.INVALID_INPUT_DATA, body.getErrorCode());
        assertTrue(body.getDescription().contains("name"));
        assertEquals(HttpStatus.BAD_REQUEST.value(), body.getStatus());
    }

    @Test
    @DisplayName("잘못된 요청 형식(파라미터 누락) → 400 + INVALID_INPUT_DATA")
    void handleBadRequest_missingParam() {
        MissingServletRequestParameterException ex =
                new MissingServletRequestParameterException("q", "String");

        var resp = handler.handleBadRequest(ex);

        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        ErrorResponse body = resp.getBody();
        assertNotNull(body);
        assertEquals(ErrorCode.INVALID_INPUT_DATA, body.getErrorCode());
        assertTrue(body.getDescription().contains("q"));
        assertEquals(HttpStatus.BAD_REQUEST.value(), body.getStatus());
    }

    @Test
    @DisplayName("잘못된 요청 형식(JSON 파싱 실패) → 400 + INVALID_INPUT_DATA")
    void handleBadRequest_unreadableBody() {
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("JSON parse error");

        var resp = handler.handleBadRequest(ex);

        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        ErrorResponse body = resp.getBody();
        assertNotNull(body);
        assertEquals(ErrorCode.INVALID_INPUT_DATA, body.getErrorCode());
        assertTrue(body.getDescription().contains("JSON"));
        assertEquals(HttpStatus.BAD_REQUEST.value(), body.getStatus());
    }

    @Test
    @DisplayName("미지원 메서드 → 405 + METHOD_NOT_ALLOWED")
    void handleMethodNotAllowed() {
        HttpRequestMethodNotSupportedException ex =
                new HttpRequestMethodNotSupportedException("POST", List.of("GET"));

        var resp = handler.handleMethodNotAllowed(ex);

        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, resp.getStatusCode());
        var body = resp.getBody();
        assertNotNull(body);
        assertEquals(ErrorCode.METHOD_NOT_ALLOWED, body.getErrorCode());
        assertTrue(body.getDescription().contains("POST")); // "Request method 'POST' is not supported"
        assertEquals(HttpStatus.METHOD_NOT_ALLOWED.value(), body.getStatus());
    }

    @Test
    @DisplayName("DB 무결성 위반 → 409 + DATA_INTEGRITY_VIOLATION")
    void handleDataIntegrity() {
        DataIntegrityViolationException ex = new DataIntegrityViolationException("duplicate key");

        var resp = handler.handleDataIntegrity(ex);

        assertEquals(HttpStatus.CONFLICT, resp.getStatusCode());
        ErrorResponse body = resp.getBody();
        assertNotNull(body);
        assertEquals(ErrorCode.DATA_INTEGRITY_VIOLATION, body.getErrorCode());
        assertEquals("데이터 무결성에 위배되었습니다.", body.getDescription());
        assertEquals(HttpStatus.CONFLICT.value(), body.getStatus());
    }

    @Test
    @DisplayName("그 밖의 예외 → 500 + INTERNAL_SERVER_ERROR")
    void handleException() {
        Exception ex = new RuntimeException("boom");

        var resp = handler.handleException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
        ErrorResponse body = resp.getBody();
        assertNotNull(body);
        assertEquals(ErrorCode.INTERNAL_SERVER_ERROR, body.getErrorCode());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), body.getStatus());
    }

    static class DummyController {
        public void create(DummyDto dto) {}
    }

    static class DummyDto {
        public String name;
    }

}