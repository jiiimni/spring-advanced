package org.example.expert.config;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class JwtAuthHelperTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private HttpServletRequest request;

    @Mock
    private Claims claims;

    @Test
    void Authorization_헤더가_없으면_예외가_발생한다() {
        // given
        JwtAuthHelper jwtAuthHelper = new JwtAuthHelper(jwtUtil);
        given(request.getHeader("Authorization")).willReturn(null);

        // when & then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> jwtAuthHelper.validateAndExtractClaims(request)
        );

        assertEquals("인증 헤더가 없습니다.", exception.getMessage());
    }

    @Test
    void Claims를_정상적으로_추출한다() {
        // given
        JwtAuthHelper jwtAuthHelper = new JwtAuthHelper(jwtUtil);
        given(request.getHeader("Authorization")).willReturn("Bearer token");
        given(jwtUtil.substringToken("Bearer token")).willReturn("token");
        given(jwtUtil.extractClaims("token")).willReturn(claims);

        // when
        Claims result = jwtAuthHelper.validateAndExtractClaims(request);

        // then
        assertEquals(claims, result);
    }

    @Test
    void Claims가_null이면_예외가_발생한다() {
        // given
        JwtAuthHelper jwtAuthHelper = new JwtAuthHelper(jwtUtil);
        given(request.getHeader("Authorization")).willReturn("Bearer token");
        given(jwtUtil.substringToken("Bearer token")).willReturn("token");
        given(jwtUtil.extractClaims("token")).willReturn(null);

        // when & then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> jwtAuthHelper.validateAndExtractClaims(request)
        );

        assertEquals("유효하지 않은 토큰입니다.", exception.getMessage());
    }

    @Test
    void 사용자_속성을_request에_저장한다() {
        // given
        JwtAuthHelper jwtAuthHelper = new JwtAuthHelper(jwtUtil);
        given(claims.getSubject()).willReturn("1");
        given(claims.get("email")).willReturn("test@test.com");
        given(claims.get("userRole")).willReturn("USER");

        // when
        jwtAuthHelper.setUserAttributes(request, claims);

        // then
        then(request).should().setAttribute("userId", 1L);
        then(request).should().setAttribute("email", "test@test.com");
        then(request).should().setAttribute("userRole", "USER");
    }

    @Test
    void 관리자_API에_ADMIN이_아니면_예외가_발생한다() {
        // given
        JwtAuthHelper jwtAuthHelper = new JwtAuthHelper(jwtUtil);
        given(claims.get("userRole", String.class)).willReturn("USER");

        // when & then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> jwtAuthHelper.validateAdminAccess("/admin/comments/1", claims)
        );

        assertEquals("접근 권한이 없습니다.", exception.getMessage());
    }

    @Test
    void 관리자_API에_ADMIN이면_통과한다() {
        // given
        JwtAuthHelper jwtAuthHelper = new JwtAuthHelper(jwtUtil);
        given(claims.get("userRole", String.class)).willReturn("ADMIN");

        // when & then
        assertDoesNotThrow(() -> jwtAuthHelper.validateAdminAccess("/admin/comments/1", claims));
    }

    @Test
    void 일반_API는_ADMIN이_아니어도_통과한다() {
        // given
        JwtAuthHelper jwtAuthHelper = new JwtAuthHelper(jwtUtil);
        given(claims.get("userRole", String.class)).willReturn("USER");

        // when & then
        assertDoesNotThrow(() -> jwtAuthHelper.validateAdminAccess("/todos", claims));
    }
}