package org.example.expert.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class WebConfigTest {

    private AuthUserArgumentResolver authUserArgumentResolver;
    private AdminApiInterceptor adminApiInterceptor;
    private WebConfig webConfig;

    @BeforeEach
    void setUp() {
        authUserArgumentResolver = new AuthUserArgumentResolver();
        adminApiInterceptor = mock(AdminApiInterceptor.class);
        webConfig = new WebConfig(authUserArgumentResolver, adminApiInterceptor);
    }

    @Test
    void argumentResolver를_등록한다() {
        // given
        List<HandlerMethodArgumentResolver> resolvers = new ArrayList<>();

        // when
        webConfig.addArgumentResolvers(resolvers);

        // then
        assertEquals(1, resolvers.size());
        assertTrue(resolvers.get(0) instanceof AuthUserArgumentResolver);
    }
}