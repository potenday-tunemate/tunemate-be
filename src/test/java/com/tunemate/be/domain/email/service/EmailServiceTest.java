package com.tunemate.be.domain.email.service;


import com.tunemate.be.global.exceptions.CustomException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@Testcontainers
@ExtendWith(SpringExtension.class)
public class EmailServiceTest {

    private EmailService emailService;

    @Mock
    private JavaMailSender emailSender;

    @Mock
    private SpringTemplateEngine templateEngine;

    @Mock
    private MimeMessage mimeMessage;

    @Container
    static MariaDBContainer<?> mariaDBContainer = new MariaDBContainer<>("mariadb:10.7")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");


    @BeforeEach
    public void setUp() {
        emailService = new EmailService(emailSender, templateEngine);
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mariaDBContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mariaDBContainer::getUsername);
        registry.add("spring.datasource.password", mariaDBContainer::getPassword);
        registry.add("spring.jpa.properties.hibernate.dialect", () -> "org.hibernate.dialect.MariaDBDialect");
        registry.add("jwt.secret", () -> "thisIsASecretKeyThatShouldBeAtLeast32BytesLongOkWhynotWork");
        registry.add("jwt.access_token.time", () -> 24);
        registry.add("jwt.refresh_token.time", () -> 48);
    }

    @Test
    public void testSendEmail_Success() throws MessagingException {
        // Arrange
        String to = "recipient@example.com";
        String subject = "Test Subject";
        String templateName = "test-template";
        Context context = new Context();
        context.setVariable("name", "Test User");

        // Mock the behavior of dependencies
        when(emailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(eq(templateName), any(Context.class)))
                .thenReturn("<html>Test Email Content</html>");

        // Act & Assert
        assertDoesNotThrow(() -> {
            emailService.sendEmail(to, subject, templateName, context);
        });

        // Verify interactions
        verify(emailSender).createMimeMessage();
        verify(templateEngine).process(eq(templateName), any(Context.class));
        verify(emailSender).send(mimeMessage);
    }

    @Test
    public void testSendEmail_MessagingException() {
        // Arrange
        String to = "recipient@example.com";
        String subject = "Test Subject";
        String templateName = "test-template";
        Context context = new Context();

        // Simulate exception when creating mime message
        when(emailSender.createMimeMessage()).thenThrow(new RuntimeException("Email creation failed"));

        // Act & Assert
        CustomException thrown = assertThrows(CustomException.class, () -> {
            emailService.sendEmail(to, subject, templateName, context);
        });

        // Verify the exception details
        assertEquals("이메일 전송에 실패했습니다.", thrown.getMessage());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, thrown.getStatusCode());
        assertEquals(2004, thrown.getErrorCode());

        // Verify interactions
        verify(emailSender).createMimeMessage();
    }

    @Test
    public void testSendEmail_TemplateProcessingFailure() throws MessagingException {
        // Arrange
        String to = "recipient@example.com";
        String subject = "Test Subject";
        String templateName = "test-template";
        Context context = new Context();

        // Mock mime message creation
        when(emailSender.createMimeMessage()).thenReturn(mimeMessage);

        // Simulate template processing failure
        when(templateEngine.process(eq(templateName), any(Context.class)))
                .thenThrow(new RuntimeException("Template processing failed"));

        // Act & Assert
        CustomException thrown = assertThrows(CustomException.class, () -> {
            emailService.sendEmail(to, subject, templateName, context);
        });

        // Verify the exception details
        assertEquals("이메일 전송에 실패했습니다.", thrown.getMessage());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, thrown.getStatusCode());
        assertEquals(2004, thrown.getErrorCode());

        // Verify interactions
        verify(emailSender).createMimeMessage();
        verify(templateEngine).process(eq(templateName), any(Context.class));
    }
}
