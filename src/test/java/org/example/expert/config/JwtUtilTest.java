package org.example.expert.config;

import io.jsonwebtoken.Claims;
import org.example.expert.domain.common.exception.ServerException;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secretKey", "aGVsbG9zcHJpbmdib290c2VjcmV0a2V5MTIzNDU2Nzg5");
        jwtUtil.init();
    }

    @Test
    void 토큰을_생성한다() {
        // when
        String token = jwtUtil.createToken(1L, "test@test.com", UserRole.USER);

        // then
        assertNotNull(token);
        assertTrue(token.startsWith("Bearer "));
    }

    @Test
    void Bearer_접두사를_제거한다() {
        // when
        String result = jwtUtil.substringToken("Bearer test.token.value");

        // then
        assertEquals("test.token.value", result);
    }

    @Test
    void Bearer_형식이_아니면_예외가_발생한다() {
        // when & then
        ServerException exception = assertThrows(
                ServerException.class,
                () -> jwtUtil.substringToken("invalid-token")
        );

        assertEquals("Not Found Token", exception.getMessage());
    }

    @Test
    void Claims를_추출한다() {
        // given
        String token = jwtUtil.createToken(1L, "test@test.com", UserRole.USER);
        String pureToken = jwtUtil.substringToken(token);

        // when
        Claims claims = jwtUtil.extractClaims(pureToken);

        // then
        assertEquals("1", claims.getSubject());
        assertEquals("test@test.com", claims.get("email"));
        assertEquals("USER", claims.get("userRole").toString());
    }
}
