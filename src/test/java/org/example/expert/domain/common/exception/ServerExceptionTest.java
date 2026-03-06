package org.example.expert.domain.common.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ServerExceptionTest {

    @Test
    void 메시지를_정상적으로_반환한다() {
        // given
        ServerException exception = new ServerException("서버 예외");

        // then
        assertEquals("서버 예외", exception.getMessage());
    }
}