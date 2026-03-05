package org.example.expert.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.expert.domain.user.enums.UserRole;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;

@Slf4j
@Component
public class AdminApiInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // JwtFilter에서 저장한 값 꺼내기
        Long userId = (Long) request.getAttribute("userId");
        String roleValue = (String) request.getAttribute("userRole"); // "ADMIN" or "USER"

        // JwtFilter에서 이미 인증을 걸러주지만 인터셉터에서도 2중 체크
        if (userId == null || roleValue == null) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "인증이 필요합니다.");
            return false;
        }

        UserRole role;
        try {
            role = UserRole.valueOf(roleValue);
        } catch (Exception e) {
            response.sendError(HttpStatus.FORBIDDEN.value(), "접근 권한이 없습니다.");
            return false;
        }

        if (!UserRole.ADMIN.equals(role)) {
            response.sendError(HttpStatus.FORBIDDEN.value(), "접근 권한이 없습니다.");
            return false;
        }

        log.info("[ADMIN API 접근] userId={}, requestTime={}, method={}, url={}",
                userId, LocalDateTime.now(), request.getMethod(), request.getRequestURI());

        return true;
    }
}