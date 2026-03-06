package org.example.expert.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

class AdminApiInterceptorTest {

    private final AdminApiInterceptor interceptor = new AdminApiInterceptor();

    @Test
    void userId가_null이면_UNAUTHORIZED를_반환한다() throws Exception {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        given(request.getAttribute("userId")).willReturn(null);
        given(request.getAttribute("userRole")).willReturn("ADMIN");

        // when
        boolean result = interceptor.preHandle(request, response, new Object());

        // then
        assertFalse(result);
        then(response).should().sendError(401, "인증이 필요합니다.");
    }

    @Test
    void roleValue가_null이면_UNAUTHORIZED를_반환한다() throws Exception {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        given(request.getAttribute("userId")).willReturn(1L);
        given(request.getAttribute("userRole")).willReturn(null);

        // when
        boolean result = interceptor.preHandle(request, response, new Object());

        // then
        assertFalse(result);
        then(response).should().sendError(401, "인증이 필요합니다.");
    }

    @Test
    void role이_잘못되면_FORBIDDEN을_반환한다() throws Exception {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        given(request.getAttribute("userId")).willReturn(1L);
        given(request.getAttribute("userRole")).willReturn("INVALID_ROLE");

        // when
        boolean result = interceptor.preHandle(request, response, new Object());

        // then
        assertFalse(result);
        then(response).should().sendError(403, "접근 권한이 없습니다.");
    }

    @Test
    void ADMIN이_아니면_FORBIDDEN을_반환한다() throws Exception {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        given(request.getAttribute("userId")).willReturn(1L);
        given(request.getAttribute("userRole")).willReturn("USER");

        // when
        boolean result = interceptor.preHandle(request, response, new Object());

        // then
        assertFalse(result);
        then(response).should().sendError(403, "접근 권한이 없습니다.");
    }

    @Test
    void ADMIN이면_true를_반환한다() throws Exception {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        given(request.getAttribute("userId")).willReturn(1L);
        given(request.getAttribute("userRole")).willReturn("ADMIN");
        given(request.getMethod()).willReturn("DELETE");
        given(request.getRequestURI()).willReturn("/admin/comments/1");

        // when
        boolean result = interceptor.preHandle(request, response, new Object());

        // then
        assertTrue(result);
    }
}
