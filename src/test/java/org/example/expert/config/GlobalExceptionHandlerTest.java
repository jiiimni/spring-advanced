package org.example.expert.config;

import org.example.expert.domain.auth.exception.AuthException;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.common.exception.ServerException;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

    @Test
    void InvalidRequestException을_처리한다() {
        // given
        InvalidRequestException exception = new InvalidRequestException("잘못된 요청");

        // when
        ResponseEntity<Map<String, Object>> response =
                globalExceptionHandler.invalidRequestExceptionException(exception);

        // then
        assertEquals(400, response.getStatusCode().value());
        assertEquals("BAD_REQUEST", response.getBody().get("status"));
        assertEquals(400, response.getBody().get("code"));
        assertEquals("잘못된 요청", response.getBody().get("message"));
    }

    @Test
    void AuthException을_처리한다() {
        // given
        AuthException exception = new AuthException("인증 실패");

        // when
        ResponseEntity<Map<String, Object>> response =
                globalExceptionHandler.handleAuthException(exception);

        // then
        assertEquals(401, response.getStatusCode().value());
        assertEquals("UNAUTHORIZED", response.getBody().get("status"));
        assertEquals(401, response.getBody().get("code"));
        assertEquals("인증 실패", response.getBody().get("message"));
    }

    @Test
    void ServerException을_처리한다() {
        // given
        ServerException exception = new ServerException("서버 오류");

        // when
        ResponseEntity<Map<String, Object>> response =
                globalExceptionHandler.handleServerException(exception);

        // then
        assertEquals(500, response.getStatusCode().value());
        assertEquals("INTERNAL_SERVER_ERROR", response.getBody().get("status"));
        assertEquals(500, response.getBody().get("code"));
        assertEquals("서버 오류", response.getBody().get("message"));
    }
}