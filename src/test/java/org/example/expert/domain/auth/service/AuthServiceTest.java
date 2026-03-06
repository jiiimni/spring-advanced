package org.example.expert.domain.auth.service;

import org.example.expert.config.JwtUtil;
import org.example.expert.config.PasswordEncoder;
import org.example.expert.domain.auth.dto.request.SigninRequest;
import org.example.expert.domain.auth.dto.request.SignupRequest;
import org.example.expert.domain.auth.dto.response.SigninResponse;
import org.example.expert.domain.auth.dto.response.SignupResponse;
import org.example.expert.domain.auth.exception.AuthException;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @Test
    void 회원가입에_성공한다() {
        // given
        SignupRequest signupRequest = new SignupRequest("test@test.com", "password123", "USER");
        String encodedPassword = "encodedPassword";
        String token = "Bearer test-token";

        User savedUser = new User("test@test.com", encodedPassword, UserRole.USER);
        ReflectionTestUtils.setField(savedUser, "id", 1L);

        given(userRepository.existsByEmail(signupRequest.getEmail())).willReturn(false);
        given(passwordEncoder.encode(signupRequest.getPassword())).willReturn(encodedPassword);
        given(userRepository.save(org.mockito.ArgumentMatchers.any(User.class))).willReturn(savedUser);
        given(jwtUtil.createToken(savedUser.getId(), savedUser.getEmail(), savedUser.getUserRole())).willReturn(token);

        // when
        SignupResponse response = authService.signup(signupRequest);

        // then
        assertNotNull(response);
    }

    @Test
    void 회원가입_시_이메일이_중복되면_예외가_발생한다() {
        // given
        SignupRequest signupRequest = new SignupRequest("test@test.com", "password123", "USER");
        given(userRepository.existsByEmail(signupRequest.getEmail())).willReturn(true);

        // when & then
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> authService.signup(signupRequest)
        );

        assertEquals("이미 존재하는 이메일입니다.", exception.getMessage());
    }

    @Test
    void 로그인에_성공한다() {
        // given
        SigninRequest signinRequest = new SigninRequest("test@test.com", "password123");
        String token = "Bearer test-token";

        User user = new User("test@test.com", "encodedPassword", UserRole.USER);
        ReflectionTestUtils.setField(user, "id", 1L);

        given(userRepository.findByEmail(signinRequest.getEmail())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(signinRequest.getPassword(), user.getPassword())).willReturn(true);
        given(jwtUtil.createToken(user.getId(), user.getEmail(), user.getUserRole())).willReturn(token);

        // when
        SigninResponse response = authService.signin(signinRequest);

        // then
        assertNotNull(response);
    }

    @Test
    void 로그인_시_가입되지_않은_유저면_예외가_발생한다() {
        // given
        SigninRequest signinRequest = new SigninRequest("test@test.com", "password123");
        given(userRepository.findByEmail(signinRequest.getEmail())).willReturn(Optional.empty());

        // when & then
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> authService.signin(signinRequest)
        );

        assertEquals("가입되지 않은 유저입니다.", exception.getMessage());
    }

    @Test
    void 로그인_시_비밀번호가_일치하지_않으면_예외가_발생한다() {
        // given
        SigninRequest signinRequest = new SigninRequest("test@test.com", "wrongPassword");

        User user = new User("test@test.com", "encodedPassword", UserRole.USER);

        given(userRepository.findByEmail(signinRequest.getEmail())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(signinRequest.getPassword(), user.getPassword())).willReturn(false);

        // when & then
        AuthException exception = assertThrows(
                AuthException.class,
                () -> authService.signin(signinRequest)
        );

        assertEquals("잘못된 비밀번호입니다.", exception.getMessage());
    }
}