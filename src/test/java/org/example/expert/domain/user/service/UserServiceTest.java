package org.example.expert.domain.user.service;

import org.example.expert.config.PasswordEncoder;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest;
import org.example.expert.domain.user.dto.response.UserResponse;
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
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void 유저_조회에_성공한다() {
        // given
        long userId = 1L;
        User user = new User("test@test.com", "encodedPassword", UserRole.USER);
        ReflectionTestUtils.setField(user, "id", userId);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        // when
        UserResponse response = userService.getUser(userId);

        // then
        assertNotNull(response);
        assertEquals(userId, response.getId());
        assertEquals("test@test.com", response.getEmail());
    }

    @Test
    void 유저_조회_시_유저가_없으면_예외가_발생한다() {
        // given
        long userId = 1L;
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> userService.getUser(userId)
        );

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void 비밀번호_변경에_성공한다() {
        // given
        long userId = 1L;
        User user = new User("test@test.com", "encodedOldPassword", UserRole.USER);

        UserChangePasswordRequest request =
                new UserChangePasswordRequest("oldPassword123", "newPassword123");

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(passwordEncoder.matches(request.getNewPassword(), user.getPassword())).willReturn(false);
        given(passwordEncoder.matches(request.getOldPassword(), user.getPassword())).willReturn(true);
        given(passwordEncoder.encode(request.getNewPassword())).willReturn("encodedNewPassword");

        // when & then
        assertDoesNotThrow(() -> userService.changePassword(userId, request));
    }

    @Test
    void 비밀번호_변경_시_유저가_없으면_예외가_발생한다() {
        // given
        long userId = 1L;
        UserChangePasswordRequest request =
                new UserChangePasswordRequest("oldPassword123", "newPassword123");

        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> userService.changePassword(userId, request)
        );

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void 비밀번호_변경_시_새_비밀번호가_기존과_같으면_예외가_발생한다() {
        // given
        long userId = 1L;
        User user = new User("test@test.com", "encodedPassword", UserRole.USER);

        UserChangePasswordRequest request =
                new UserChangePasswordRequest("oldPassword123", "samePassword123");

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(passwordEncoder.matches(request.getNewPassword(), user.getPassword())).willReturn(true);

        // when & then
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> userService.changePassword(userId, request)
        );

        assertEquals("새 비밀번호는 기존 비밀번호와 같을 수 없습니다.", exception.getMessage());
    }

    @Test
    void 비밀번호_변경_시_기존_비밀번호가_틀리면_예외가_발생한다() {
        // given
        long userId = 1L;
        User user = new User("test@test.com", "encodedPassword", UserRole.USER);

        UserChangePasswordRequest request =
                new UserChangePasswordRequest("wrongOldPassword", "newPassword123");

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(passwordEncoder.matches(request.getNewPassword(), user.getPassword())).willReturn(false);
        given(passwordEncoder.matches(request.getOldPassword(), user.getPassword())).willReturn(false);

        // when & then
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> userService.changePassword(userId, request)
        );

        assertEquals("잘못된 비밀번호입니다.", exception.getMessage());
    }
}