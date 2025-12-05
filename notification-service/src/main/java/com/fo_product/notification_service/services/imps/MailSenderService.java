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

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MailSenderService implements IMailSenderService {
    JavaMailSender javaMailSender;
    TemplateEngine templateEngine;

    @NonFinal
    @Value("${spring.mail.username}")
    String mailFrom; // Đổi tên biến cho rõ nghĩa

    @Async // Nhớ thêm @EnableAsync ở file Main Application
    @Override
    public void sendOtpEmail(String to, String otp, String type) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            // Cấu hình mã hóa UTF-8 để không lỗi font tiếng Việt
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

            helper.setFrom(mailFrom); // QUAN TRỌNG: Phải có dòng này
            helper.setTo(to);
            helper.setSubject(type);

            Context context = new Context();
            context.setVariable("otpCode", otp);
            context.setVariable("action", type);

            String htmlContent = templateEngine.process("otp-email", context);
            helper.setText(htmlContent, true);

            // Đảm bảo file ảnh tồn tại trong folder: src/main/resources/static/images/
            ClassPathResource imageResource = new ClassPathResource("static/images/logo-fo-app.png");
            if (imageResource.exists()) {
                helper.addInline("logoImage", imageResource);
            } else {
                log.warn("Logo image not found in resources!");
            }

            javaMailSender.send(message);
            log.info("Email sent successfully to: {}", to);

        } catch (MessagingException e) {
            // Với @Async, không throw exception ra ngoài vì main thread đã chạy xong rồi
            // Chỉ log lỗi để debug
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }
}