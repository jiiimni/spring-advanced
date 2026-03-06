package org.example.expert.domain.comment.dto.response;

import org.example.expert.domain.user.dto.response.UserResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CommentResponseTest {

    @Test
    void 필드를_정상적으로_반환한다() {
        // given
        UserResponse userResponse = new UserResponse(1L, "test@test.com");
        CommentResponse response = new CommentResponse(10L, "댓글 내용", userResponse);

        // then
        assertEquals(10L, response.getId());
        assertEquals("댓글 내용", response.getContents());
        assertEquals(1L, response.getUser().getId());
        assertEquals("test@test.com", response.getUser().getEmail());
    }
}