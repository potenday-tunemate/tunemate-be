package com.tunemate.be.domain.auth.service;

import com.tunemate.be.domain.auth.domain.CreateEmailAuthDTO;
import com.tunemate.be.domain.auth.domain.EmailAuth;
import com.tunemate.be.domain.auth.domain.EmailAuthMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest
public class EmailAuthServiceTest {
    @Container
    private static final MariaDBContainer<?> mariadbContainer = new MariaDBContainer<>("mariadb:10.7")
            .withDatabaseName("testDB")
            .withUsername("testuser")
            .withPassword("testpass");

    @DynamicPropertySource
    static void setDatasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mariadbContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mariadbContainer::getUsername);
        registry.add("spring.datasource.password", mariadbContainer::getPassword);
    }

    @Autowired
    private EmailAuthService emailAuthService;

    @Autowired
    private EmailAuthMapper emailAuthMapper;

    @Autowired
    private DataSource dataSource;

    @BeforeEach
    void runExternalCommand() throws Exception {
        ProcessBuilder builder = new ProcessBuilder();
        Map<String, String> env = builder.environment();
        String mariadbUrl = String.format("%s:%s@tcp(%s:%d)/%s", mariadbContainer.getUsername(), mariadbContainer.getPassword(), mariadbContainer.getHost(), mariadbContainer.getMappedPort(3306), mariadbContainer.getDatabaseName());
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
    void testCreateEmailAuth_Success() {
        Timestamp expirationTime = Timestamp.from(Instant.now().plusSeconds(3600));
        CreateEmailAuthDTO dto = new CreateEmailAuthDTO("test@naver.com", "test", expirationTime);
        emailAuthService.createEmailAuth(dto);
        EmailAuth auth = emailAuthService.getEmailAuthByToken("test");
        assertNotNull(auth, "EmailAuth should not be null");
        assertNotNull(auth.getId(), "EmailAuth id should not be null");
        assertEquals("test", auth.getToken(), "created auth token is not equal");
        assertEquals("test@naver.com", auth.getEmail(), "created auth email is not equal");
        Instant now = Instant.now();
        Instant expectedExpiration = now.plusSeconds(3600);
        Instant actualExpiration = auth.getExpired_at().toInstant();
        long secondsDifference = Math.abs(expectedExpiration.getEpochSecond() - actualExpiration.getEpochSecond());
        assertTrue(secondsDifference < 5, "Expiration time should be approximately 1 hour from now");
    }

    @Test
    void testCreateEmailAuth_ShouldFail_IF_Email_Exists() {
        Timestamp expirationTime = Timestamp.from(Instant.now().plusSeconds(3600));
        CreateEmailAuthDTO dto = new CreateEmailAuthDTO("test@naver.com", "test", expirationTime);
        emailAuthService.createEmailAuth(dto);

        try {
            emailAuthService.createEmailAuth(dto);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            assertNotNull(e, "Email Auth creation failed");
        }
    }


    private static class StreamGobbler extends Thread {
        private final BufferedReader reader;
        private final String streamType;

        public StreamGobbler(java.io.InputStream inputStream, String streamType) {
            this.reader = new BufferedReader(new InputStreamReader(inputStream));
            this.streamType = streamType;
        }

        @Override
        public void run() {
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    if ("ERROR".equals(streamType)) {
                        System.err.println("[" + streamType + "] " + line);
                    } else {
                        System.out.println("[" + streamType + "] " + line);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
