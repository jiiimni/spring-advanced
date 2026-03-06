package org.example.expert.config;

import jakarta.servlet.http.HttpServletRequest;
import org.example.expert.domain.auth.exception.AuthException;
import org.example.expert.domain.common.annotation.Auth;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class AuthUserArgumentResolverTest {

    private AuthUserArgumentResolver resolver;

    @BeforeEach
    void setUp() {
        resolver = new AuthUserArgumentResolver();
    }

    static class TestController {
        public void validMethod(@Auth AuthUser authUser) {}
        public void invalidMethod1(@Auth String value) {}
        public void invalidMethod2(AuthUser authUser) {}
    }

    @Test
    void Auth와_AuthUser를_함께_사용하면_true를_반환한다() throws Exception {
        // given
        Method method = TestController.class.getMethod("validMethod", AuthUser.class);
        MethodParameter parameter = new MethodParameter(method, 0);

        // when
        boolean result = resolver.supportsParameter(parameter);

        // then
        assertTrue(result);
    }

    @Test
    void Auth는_있지만_AuthUser가_아니면_예외가_발생한다() throws Exception {
        // given
        Method method = TestController.class.getMethod("invalidMethod1", String.class);
        MethodParameter parameter = new MethodParameter(method, 0);

        // when & then
        AuthException exception = assertThrows(
                AuthException.class,
                () -> resolver.supportsParameter(parameter)
        );

        assertEquals("@Auth와 AuthUser 타입은 함께 사용되어야 합니다.", exception.getMessage());
    }

    @Test
    void AuthUser는_있지만_Auth가_없으면_예외가_발생한다() throws Exception {
        // given
        Method method = TestController.class.getMethod("invalidMethod2", AuthUser.class);
        MethodParameter parameter = new MethodParameter(method, 0);

        // when & then
        AuthException exception = assertThrows(
                AuthException.class,
                () -> resolver.supportsParameter(parameter)
        );

        assertEquals("@Auth와 AuthUser 타입은 함께 사용되어야 합니다.", exception.getMessage());
    }

    @Test
    void request에서_값을_꺼내_AuthUser를_생성한다() {
        // given
        NativeWebRequest webRequest = mock(NativeWebRequest.class);
        HttpServletRequest request = mock(HttpServletRequest.class);

        given(webRequest.getNativeRequest()).willReturn(request);
        given(request.getAttribute("userId")).willReturn(1L);
        given(request.getAttribute("email")).willReturn("test@test.com");
        given(request.getAttribute("userRole")).willReturn("USER");

        // when
        Object result = resolver.resolveArgument(null, null, webRequest, null);

        // then
        assertInstanceOf(AuthUser.class, result);

        AuthUser authUser = (AuthUser) result;
        assertEquals(1L, authUser.getId());
        assertEquals("test@test.com", authUser.getEmail());
        assertEquals(UserRole.USER, authUser.getUserRole());
    }
}