package com.fo_product.merchant_service.services.imps.wallet;

import com.fo_product.merchant_service.dtos.responses.wallet.WalletTransactionResponse;
import com.fo_product.merchant_service.mappers.wallet.WalletMapper;
import com.fo_product.merchant_service.models.entities.wallet.WalletTransaction;
import com.fo_product.merchant_service.models.enums.TransactionType;
import com.fo_product.merchant_service.models.repositories.wallet.WalletTransactionRepository;
import com.fo_product.merchant_service.services.interfaces.wallet.IAdminWalletService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminWalletService implements IAdminWalletService {
    WalletTransactionRepository walletTransactionRepository;
    WalletMapper walletMapper;

    @Override
    public Page<WalletTransactionResponse> getAllTransactions(int page, int size, Instant startDate, Instant endDate, TransactionType type) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Specification<WalletTransaction> spec = Specification.where((root, query, cb) -> cb.conjunction());

        if (startDate != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("createdAt"), startDate.atZone(ZoneOffset.UTC).toLocalDateTime()));
        }
        if (endDate != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("createdAt"), endDate.atZone(ZoneOffset.UTC).toLocalDateTime()));
        }
        if (type != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("transactionType"), type));
        }

        return walletTransactionRepository.findAll(spec, pageable).map(walletMapper::response);
    }
}
