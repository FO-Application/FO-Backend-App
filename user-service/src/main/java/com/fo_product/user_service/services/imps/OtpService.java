package com.fo_product.user_service.services.imps;

import com.fo_product.user_service.exceptions.code.UserErrorCode;
import com.fo_product.user_service.exceptions.UserException;
import com.fo_product.user_service.kafka.KafkaProducerService;
import com.fo_product.user_service.kafka.events.MailSenderEvent;
import com.fo_product.user_service.models.enums.OtpTokenType;
import com.fo_product.user_service.models.hashes.OtpToken;
import com.fo_product.user_service.models.repositories.OtpTokenRepository;
import com.fo_product.user_service.services.interfaces.IOtpService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OtpService implements IOtpService {
    RedisTemplate<String, String> redisTemplate;
    KafkaProducerService kafkaProducerService;
    OtpTokenRepository otpTokenRepository;

    @Override
    public void generateAndSendOtp(String email, OtpTokenType type) {
        String otp = String.format("%06d", new Random().nextInt(999999));

        OtpToken otpCode = OtpToken.builder()
                .email(email)
                .otpCode(otp)
                .type(type)
                .expiredTime(5L)
                .build();

        OtpToken result = otpTokenRepository.save(otpCode);

        MailSenderEvent event = MailSenderEvent.builder()
                .recipientEmail(result.getEmail())
                .subject(result.getType().getMessage())
                .otpCode(result.getOtpCode())
                .eventType(type.name())
                .build();

        kafkaProducerService.sendMailOTP(event);
    }

    @Override
    public boolean verifyOtp(String email, String otp) {
        OtpToken otpToken = otpTokenRepository.findById(email)
                .orElseThrow(() -> new UserException(UserErrorCode.OTP_NOT_EXIST));

        if (otpToken.getOtpCode().equals(otp)) {
            otpTokenRepository.delete(otpToken);
            return true;
        }

        return false;
    }
}
