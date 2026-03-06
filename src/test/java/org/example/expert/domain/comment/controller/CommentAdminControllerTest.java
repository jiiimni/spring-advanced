package org.example.expert.domain.comment.controller;

import org.example.expert.domain.comment.service.CommentAdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CommentAdminControllerTest {

    @Mock
    private CommentAdminService commentAdminService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        CommentAdminController controller = new CommentAdminController(commentAdminService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void 댓글_삭제_요청에_성공한다() throws Exception {
        // when & then
        mockMvc.perform(delete("/admin/comments/1"))
                .andExpect(status().isOk());

        then(commentAdminService).should().deleteComment(1L);
    }
}