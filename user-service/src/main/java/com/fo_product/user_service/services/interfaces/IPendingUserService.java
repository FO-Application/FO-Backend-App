package com.fo_product.user_service.services.interfaces;

import com.fo_product.user_service.models.hashes.PendingUser;
import com.fo_product.user_service.resources.responses.PendingUserResponse;

public interface IPendingUserService {
    PendingUserResponse savePendingUser(PendingUser pendingUser);
    PendingUser getPendingUser(String email);
}
