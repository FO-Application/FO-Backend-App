package com.fo_product.user_service.models.repositories;

import com.fo_product.user_service.models.hashes.OtpToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OtpTokenRepository extends CrudRepository<OtpToken, String> {
}
