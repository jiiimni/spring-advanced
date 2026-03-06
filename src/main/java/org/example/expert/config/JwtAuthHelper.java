package org.example.expert.config;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.user.enums.UserRole;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtAuthHelper {

    private final JwtUtil jwtUtil;

    public Claims validateAndExtractClaims(HttpServletRequest request) {
        String bearerJwt = request.getHeader("Authorization");

        if (bearerJwt == null) {
            throw new IllegalArgumentException("인증 헤더가 없습니다.");
        }

        String jwt = jwtUtil.substringToken(bearerJwt);
        Claims claims = jwtUtil.extractClaims(jwt);

        if (claims == null) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }

        return claims;
    }

    public void setUserAttributes(HttpServletRequest request, Claims claims) {
        request.setAttribute("userId", Long.parseLong(claims.getSubject()));
        request.setAttribute("email", claims.get("email"));
        request.setAttribute("userRole", claims.get("userRole"));
    }

    public void validateAdminAccess(String url, Claims claims) {
        UserRole userRole = UserRole.valueOf(claims.get("userRole", String.class));

        if (url.startsWith("/admin") && !UserRole.ADMIN.equals(userRole)) {
            throw new IllegalArgumentException("접근 권한이 없습니다.");
        }
    }
}