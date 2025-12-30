package com.fo_product.notification_service.services.imps;

import com.fo_product.notification_service.events.OrderDeliveringEvent;
import com.fo_product.notification_service.services.interfaces.IMailSenderService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MailSenderService implements IMailSenderService {
    JavaMailSender javaMailSender;
    TemplateEngine templateEngine;

    @NonFinal
    @Value("${spring.mail.username}")
    String mailFrom;

    @Async
    @Override
    public void sendOtpEmail(String to, String otp, String type) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

            helper.setFrom(mailFrom);
            helper.setTo(to);
            helper.setSubject(type);

            Context context = new Context();
            context.setVariable("otpCode", otp);
            context.setVariable("action", type);

            String htmlContent = templateEngine.process("otp-email", context);
            helper.setText(htmlContent, true);

            javaMailSender.send(message);
            log.info("Email sent successfully to: {}", to);

        } catch (MessagingException e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }

    @Async
    @Override
    public void sendDeliverMail(OrderDeliveringEvent event) {
        String to = event.customerEmail();

        try {
            log.info("Preparing to send delivery email to: {}", to);

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

            helper.setFrom(mailFrom);
            helper.setTo(to);
            helper.setSubject("Đơn hàng #" + event.orderId() + " đang được giao đến bạn!");

            // Định dạng tiền tệ VNĐ
            NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            String formattedAmount = currencyFormatter.format(event.amount());

            // Đưa dữ liệu vào Context
            Context context = new Context();
            context.setVariable("customerName", event.customerName());
            context.setVariable("merchantName", event.merchantName());
            context.setVariable("orderId", event.orderId());
            context.setVariable("productName", event.productName());
            context.setVariable("amount", formattedAmount);
            context.setVariable("deliveryAddress", event.deliveryAddress());

            String htmlContent = templateEngine.process("order-delivering", context);
            helper.setText(htmlContent, true);

            javaMailSender.send(message);
            log.info("Delivery email sent successfully to: {}", to);

        } catch (MessagingException e) {
            log.error("Failed to send delivery email to {}: {}", to, e.getMessage());
        }
    }
}