package org.example.expert.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.mock;

class JwtFilterTest {

    private JwtAuthHelper jwtAuthHelper;
    private ObjectMapper objectMapper;
    private JwtFilter jwtFilter;

    @BeforeEach
    void setUp() {
        jwtAuthHelper = mock(JwtAuthHelper.class);
        objectMapper = new ObjectMapper();
        jwtFilter = new JwtFilter(jwtAuthHelper, objectMapper);
    }

    @Test
    void auth_요청은_바로_통과한다() throws Exception {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        given(request.getRequestURI()).willReturn("/auth/signin");

        // when
        jwtFilter.doFilter(request, response, chain);

        // then
        then(chain).should().doFilter(request, response);
        then(jwtAuthHelper).shouldHaveNoInteractions();
    }

    @Test
    void 권한_없음_예외면_403을_반환한다() throws Exception {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        given(request.getRequestURI()).willReturn("/admin/comments/1");
        given(jwtAuthHelper.validateAndExtractClaims(request))
                .willThrow(new IllegalArgumentException("접근 권한이 없습니다."));
        given(response.getWriter()).willReturn(new java.io.PrintWriter(System.out));

        // when
        jwtFilter.doFilter(request, response, chain);

        // then
        then(response).should().setStatus(403);
        then(chain).should(never()).doFilter(request, response);
    }

    @Test
    void 일반_인증_예외면_401을_반환한다() throws Exception {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        given(request.getRequestURI()).willReturn("/todos");
        given(jwtAuthHelper.validateAndExtractClaims(request))
                .willThrow(new IllegalArgumentException("인증 헤더가 없습니다."));
        given(response.getWriter()).willReturn(new java.io.PrintWriter(System.out));

        // when
        jwtFilter.doFilter(request, response, chain);

        // then
        then(response).should().setStatus(401);
        then(chain).should(never()).doFilter(request, response);
    }

    @Test
    void 정상_요청이면_claims를_검증하고_통과한다() throws Exception {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);
        Claims claims = mock(Claims.class);

        given(request.getRequestURI()).willReturn("/todos");
        given(jwtAuthHelper.validateAndExtractClaims(request)).willReturn(claims);

        // when
        jwtFilter.doFilter(request, response, chain);

        // then
        then(jwtAuthHelper).should().setUserAttributes(request, claims);
        then(jwtAuthHelper).should().validateAdminAccess("/todos", claims);
        then(chain).should().doFilter(request, response);
    }
}