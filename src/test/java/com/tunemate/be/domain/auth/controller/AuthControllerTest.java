package com.tunemate.be.domain.auth.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.tunemate.be.domain.auth.domain.auth.SigninDTO;
import com.tunemate.be.domain.user.domain.user.CreateUserDTO;
import com.tunemate.be.utils.StreamGobbler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@Transactional
@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {
    @Container
    static MariaDBContainer<?> mariaDBContainer = new MariaDBContainer<>("mariadb:10.7")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mariaDBContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mariaDBContainer::getUsername);
        registry.add("spring.datasource.password", mariaDBContainer::getPassword);
        registry.add("jwt.secret", () -> "thisIsASecretKeyThatShouldBeAtLeast32BytesLongOkWhynotWork");
        registry.add("jwt.access_token.time", () -> 24);
        registry.add("jwt.refresh_token.time", () -> 48);
    }

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder();
        Map<String, String> env = builder.environment();
        String mariadbUrl = String.format("%s:%s@tcp(%s:%d)/%s", mariaDBContainer.getUsername(), mariaDBContainer.getPassword(), mariaDBContainer.getHost(), mariaDBContainer.getMappedPort(3306), mariaDBContainer.getDatabaseName());
        System.out.println(mariadbUrl);
        env.put("GOOSE_MIGRATION_DIR", "./migrations");

        builder.command("goose", "mysql", mariadbUrl, "up");

        Process process = builder.start();


        StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream(), "OUTPUT");
        StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), "ERROR");

        outputGobbler.start();
        errorGobbler.start();

        int exitCode = process.waitFor();

        outputGobbler.join();
        errorGobbler.join();

        if (exitCode != 0) {
            throw new RuntimeException("Migration failed with exit code: " + exitCode);
        }
    }

    @Test
    void testSignUp() throws Exception {
        CreateUserDTO dto = CreateUserDTO.builder()
                .email("test@example.com")
                .password("password")
                .nickname(null)
                .build();

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ok").value(true));
    }

    @Test
    void testSignIn() throws Exception {
        CreateUserDTO signupDto = CreateUserDTO.builder()
                .email("login@example.com")
                .password("password")
                .nickname(null)
                .build();

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(signupDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ok").value(true));

        SigninDTO signinDTO = SigninDTO.builder()
                .email("login@example.com")
                .password("password")
                .build();

        mockMvc.perform(post("/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(signinDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ok").value(true))
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(jsonPath("$.data.refreshToken").exists());
    }

    private static String asJsonString(Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
