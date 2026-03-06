package org.example.expert.domain.todo.service;

import org.example.expert.client.WeatherClient;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;

    @Mock
    private WeatherClient weatherClient;

    @InjectMocks
    private TodoService todoService;

    @Test
    void 할일_등록에_성공한다() {
        // given
        AuthUser authUser = new AuthUser(1L, "test@test.com", UserRole.USER);
        TodoSaveRequest request = new TodoSaveRequest("제목", "내용");

        given(weatherClient.getTodayWeather()).willReturn("맑음");

        Todo savedTodo = new Todo("제목", "내용", "맑음", User.fromAuthUser(authUser));
        ReflectionTestUtils.setField(savedTodo, "id", 1L);

        given(todoRepository.save(any(Todo.class))).willReturn(savedTodo);

        // when
        TodoSaveResponse response = todoService.saveTodo(authUser, request);

        // then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("제목", response.getTitle());
        assertEquals("내용", response.getContents());
        assertEquals("맑음", response.getWeather());
        assertEquals(authUser.getId(), response.getUser().getId());
        assertEquals(authUser.getEmail(), response.getUser().getEmail());
    }

    @Test
    void 할일_목록_조회에_성공한다() {
        // given
        int page = 1;
        int size = 10;

        User user = new User("test@test.com", "password", UserRole.USER);
        ReflectionTestUtils.setField(user, "id", 1L);

        Todo todo = new Todo("제목", "내용", "맑음", user);
        ReflectionTestUtils.setField(todo, "id", 1L);
        ReflectionTestUtils.setField(todo, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(todo, "modifiedAt", LocalDateTime.now());

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Todo> todoPage = new PageImpl<>(List.of(todo), pageable, 1);

        given(todoRepository.findAllByOrderByModifiedAtDesc(pageable)).willReturn(todoPage);

        // when
        Page<TodoResponse> result = todoService.getTodos(page, size);

        // then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("제목", result.getContent().get(0).getTitle());
        assertEquals("내용", result.getContent().get(0).getContents());
        assertEquals("맑음", result.getContent().get(0).getWeather());
        assertEquals(1L, result.getContent().get(0).getUser().getId());
        assertEquals("test@test.com", result.getContent().get(0).getUser().getEmail());
    }

    @Test
    void 할일_단건_조회에_성공한다() {
        // given
        long todoId = 1L;

        User user = new User("test@test.com", "password", UserRole.USER);
        ReflectionTestUtils.setField(user, "id", 1L);

        Todo todo = new Todo("제목", "내용", "맑음", user);
        ReflectionTestUtils.setField(todo, "id", todoId);
        ReflectionTestUtils.setField(todo, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(todo, "modifiedAt", LocalDateTime.now());

        given(todoRepository.findById(todoId)).willReturn(Optional.of(todo));

        // when
        TodoResponse response = todoService.getTodo(todoId);

        // then
        assertNotNull(response);
        assertEquals(todoId, response.getId());
        assertEquals("제목", response.getTitle());
        assertEquals("내용", response.getContents());
        assertEquals("맑음", response.getWeather());
        assertEquals(1L, response.getUser().getId());
        assertEquals("test@test.com", response.getUser().getEmail());
    }

    @Test
    void 할일_단건_조회_시_존재하지_않으면_예외가_발생한다() {
        // given
        long todoId = 1L;
        given(todoRepository.findById(todoId)).willReturn(Optional.empty());

        // when & then
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> todoService.getTodo(todoId)
        );

        assertEquals("Todo not found", exception.getMessage());
    }
}