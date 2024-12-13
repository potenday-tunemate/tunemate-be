package com.tunemate.be.domain.email.service;

import com.tunemate.be.global.exceptions.CustomException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
public class EmailServiceTest {


    @Autowired
    private EmailService emailService;

    @MockitoBean
    private JavaMailSender javaMailSender;

    @MockitoBean
    private SpringTemplateEngine templateEngine;

    @Test
    void shouldSendEmailWithHtmlContent() throws MessagingException, IOException {
        // Given
        String to = "test@example.com";
        String subject = "Test Subject";
        String templateName = "email-template";
        Context context = new Context();
        context.setVariable("name", "홍길동");
        context.setVariable("content", "이메일 내용");

        String expectedHtmlContent = "<html><body><h1>안녕하세요, 홍길동님!</h1><p>이메일 내용</p></body></html>";

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

        when(templateEngine.process(templateName, context)).thenReturn(expectedHtmlContent);

        emailService.sendEmail(to, subject, templateName, context);

        ArgumentCaptor<MimeMessage> mimeMessageCaptor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(javaMailSender).send(mimeMessageCaptor.capture());

    }

    @Test
    void shouldThrowCustomExceptionWhenEmailSendingFails() {
        String to = "test@example.com";
        String subject = "Test Subject";
        String templateName = "email-template";
        Context context = new Context();

        when(javaMailSender.createMimeMessage()).thenThrow(new RuntimeException("Failed to create message"));

        CustomException exception = assertThrows(CustomException.class, () -> {
            emailService.sendEmail(to, subject, templateName, context);
        });

        assertEquals("이메일 전송에 실패했습니다.", exception.getMessage());
        assertEquals(2004, exception.getErrorCode());
    }
}
