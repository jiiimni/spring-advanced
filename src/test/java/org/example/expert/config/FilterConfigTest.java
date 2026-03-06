package org.example.expert.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class FilterConfigTest {

    private JwtAuthHelper jwtAuthHelper;
    private ObjectMapper objectMapper;
    private FilterConfig filterConfig;

    @BeforeEach
    void setUp() {
        jwtAuthHelper = mock(JwtAuthHelper.class);
        objectMapper = new ObjectMapper();
        filterConfig = new FilterConfig(jwtAuthHelper, objectMapper);
    }

    @Test
    void jwtFilter를_등록한다() {
        // when
        FilterRegistrationBean<JwtFilter> registrationBean = filterConfig.jwtFilter();

        // then
        assertNotNull(registrationBean);
        assertNotNull(registrationBean.getFilter());
        assertInstanceOf(JwtFilter.class, registrationBean.getFilter());
        assertTrue(registrationBean.getUrlPatterns().contains("/*"));
    }
}