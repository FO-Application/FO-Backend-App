package com.fo_product.user_service.models.repositories;

import com.fo_product.user_service.models.hashes.PendingUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PendingUserRepository extends CrudRepository<PendingUser, String> {
}
