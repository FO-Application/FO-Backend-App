package com.fo_product.user_service.services.imps;

import com.fo_product.user_service.exceptions.code.UserErrorCode;
import com.fo_product.user_service.exceptions.UserException;
import com.fo_product.user_service.models.hashes.PendingUser;
import com.fo_product.user_service.models.repositories.PendingUserRepository;
import com.fo_product.user_service.dtos.responses.PendingUserResponse;
import com.fo_product.user_service.services.interfaces.IPendingUserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PendingUserService implements IPendingUserService {
    PendingUserRepository pendingUserRepository;

    @Override
    public PendingUserResponse savePendingUser(PendingUser pendingUser) {
        PendingUser result = pendingUserRepository.save(pendingUser);
        log.info("Pending user saved for: {}", pendingUser.getEmail());

        return PendingUserResponse.builder()
                .email(result.getEmail())
                .firstName(result.getFirstName())
                .lastName(result.getLastName())
                .dob(result.getDob())
                .phone(result.getPhone())
                .status("PENDING_VERIFICATION")
                .build();
    }

    @Override
    public PendingUser getPendingUser(String email) {
        return pendingUserRepository.findById(email)
                .orElseThrow(() -> new UserException(UserErrorCode.PENDING_USER_NOT_FOUND));
    }
}
