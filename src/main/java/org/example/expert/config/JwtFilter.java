package org.example.expert.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.expert.domain.user.enums.UserRole;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter implements Filter {

    private final JwtAuthHelper jwtAuthHelper;
    private final ObjectMapper objectMapper;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String url = httpRequest.getRequestURI();

        if (url.startsWith("/auth")) {
            chain.doFilter(request, response);
            return;
        }

        try {
            Claims claims = jwtAuthHelper.validateAndExtractClaims(httpRequest);

            jwtAuthHelper.setUserAttributes(httpRequest, claims);

            jwtAuthHelper.validateAdminAccess(url, claims);

            chain.doFilter(request, response);

        } catch (IllegalArgumentException e) {
            log.warn("인증/인가 실패: URI={}, message={}", url, e.getMessage());

            HttpStatus status = "접근 권한이 없습니다.".equals(e.getMessage())
                    ? HttpStatus.FORBIDDEN
                    : HttpStatus.UNAUTHORIZED;

            sendErrorResponse(httpResponse, status, e.getMessage());

        } catch (ExpiredJwtException e) {
            log.info("JWT 만료: userId={}, URI={}", e.getClaims().getSubject(), url);
            sendErrorResponse(httpResponse, HttpStatus.UNAUTHORIZED, "인증이 필요합니다.");

        } catch (SecurityException | MalformedJwtException | UnsupportedJwtException e) {
            log.error("JWT 검증 실패 [{}]: URI={}", e.getClass().getSimpleName(), url, e);
            sendErrorResponse(httpResponse, HttpStatus.BAD_REQUEST, "인증이 필요합니다.");

        } catch (Exception e) {
            log.error("예상치 못한 오류: URI={}", url, e);
            sendErrorResponse(httpResponse, HttpStatus.INTERNAL_SERVER_ERROR, "요청 처리 중 오류가 발생했습니다.");
        }
    }

    private void sendErrorResponse(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json;charset=UTF-8");

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", status.name());
        errorResponse.put("code", status.value());
        errorResponse.put("message", message);

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}