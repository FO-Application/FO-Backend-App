package com.fo_product.notification_service.services.imps;

import com.fo_product.notification_service.services.interfaces.IMailSenderService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MailSenderService implements IMailSenderService {
    JavaMailSender javaMailSender;
    TemplateEngine templateEngine;

    @NonFinal
    @Value("${spring.mail.username}")
    String mail;

    @Async
    @Override
    public void sendOtpEmail(String to, String otp, String type) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(type);

            Context context = new Context();
            context.setVariable("otpCode", otp);
            context.setVariable("action", type);

            String htmlContent = templateEngine.process("otp-email", context);
            helper.setText(htmlContent, true);

            ClassPathResource imageResource = new ClassPathResource("static/images/logo-fo-app.png");
            helper.addInline("logoImage", imageResource);

            javaMailSender.send(message);
            log.info("Email send to: {}", to);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
