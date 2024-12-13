package com.tunemate.be.domain.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tunemate.be.domain.auth.domain.emailAuth.EmailAuth;
import com.tunemate.be.domain.auth.domain.emailAuth.EmailAuthMapper;
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
import java.sql.Timestamp;
import java.time.Instant;
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
public class EmailAuthControllerTest {
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
    MockMvc mockMvc;

    @Autowired
    EmailAuthMapper emailAuthMapper; // 실제 Mapper 주입 (MyBatis 또는 JPA환경 가정)

    private final ObjectMapper objectMapper = new ObjectMapper();


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
    void testCreateEmailAuth() throws Exception {
        EmailAuth dto = EmailAuth.builder().email("test@example.com").build();

        mockMvc.perform(post("/auth/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ok").value(true));

    }

    @Test
    void testVerifyEmailAuth() throws Exception {
        EmailAuth dto = EmailAuth.builder().email("test@example.com").token("test").expired_at(Timestamp.from(Instant.now().plusSeconds(3600))).build();
        emailAuthMapper.create(dto);

        mockMvc.perform(post("/auth/email/verify")
                        .param("token", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").value("test"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"));
    }


}

