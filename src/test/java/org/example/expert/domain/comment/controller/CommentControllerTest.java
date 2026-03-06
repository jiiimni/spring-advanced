package org.example.expert.domain.comment.controller;

import org.example.expert.domain.comment.dto.response.CommentResponse;
import org.example.expert.domain.comment.service.CommentService;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CommentControllerTest {

    @Mock
    private CommentService commentService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        CommentController controller = new CommentController(commentService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void 댓글_목록_조회에_성공한다() throws Exception {
        // given
        CommentResponse response = new CommentResponse(
                1L,
                "댓글",
                new UserResponse(1L, "test@test.com")
        );

        given(commentService.getComments(1L)).willReturn(List.of(response));

        // when & then
        mockMvc.perform(get("/todos/1/comments"))
                .andExpect(status().isOk());
    }
}
