package org.example.expert.domain.user.dto.response;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserSaveResponseTest {

    @Test
    void bearerToken을_정상적으로_반환한다() {
        // given
        UserSaveResponse response = new UserSaveResponse("Bearer token");

        // then
        assertEquals("Bearer token", response.getBearerToken());
    }
}