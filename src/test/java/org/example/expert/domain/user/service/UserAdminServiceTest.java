package org.example.expert.domain.user.service;

import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.dto.request.UserRoleChangeRequest;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserAdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserAdminService userAdminService;

    @Test
    void 유저_권한_변경에_성공한다() {
        // given
        long userId = 1L;
        User user = new User("test@test.com", "password", UserRole.USER);
        UserRoleChangeRequest request = new UserRoleChangeRequest("ADMIN");

        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        // when & then
        assertDoesNotThrow(() -> userAdminService.changeUserRole(userId, request));
        assertEquals(UserRole.ADMIN, user.getUserRole());
    }

    @Test
    void 유저_권한_변경_시_유저가_없으면_예외가_발생한다() {
        // given
        long userId = 1L;
        UserRoleChangeRequest request = new UserRoleChangeRequest("ADMIN");

        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> userAdminService.changeUserRole(userId, request)
        );

        assertEquals("User not found", exception.getMessage());
    }
}