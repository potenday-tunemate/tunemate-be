package com.tunemate.be.domain.email.service;

import com.tunemate.be.global.exceptions.CustomException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
public class EmailService {

    private final JavaMailSender emailSender;
    private final SpringTemplateEngine templateEngine;

    public EmailService(JavaMailSender javaMailSender, SpringTemplateEngine templateEngine) {
        this.emailSender = javaMailSender;
        this.templateEngine = templateEngine;
    }

    public void sendEmail(
            String to, String subject, String templateName, Context context) throws MessagingException {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);

            String htmlContent = templateEngine.process(templateName, context);
            System.out.println(htmlContent);
            helper.setText(htmlContent, true);
            emailSender.send(message);
        } catch (Exception e) {
            throw new CustomException("이메일 전송에 실패했습니다.", HttpStatus.UNPROCESSABLE_ENTITY, 2004);
        }

    }
}
