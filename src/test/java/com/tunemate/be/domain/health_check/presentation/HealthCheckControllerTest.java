package com.tunemate.be.domain.health_check.presentation;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HealthCheckController.class)
public class HealthCheckControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void healthCheckTest_should_return_200_ok() throws Exception {
        mockMvc.perform(get("/health_check")).andExpect(status().isOk());

    }
}
