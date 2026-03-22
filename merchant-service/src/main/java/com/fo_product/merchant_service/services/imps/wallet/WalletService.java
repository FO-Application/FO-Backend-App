package com.fo_product.merchant_service.services.imps.wallet;

import com.fo_product.merchant_service.dtos.responses.wallet.DailyStatResponse;
import com.fo_product.merchant_service.dtos.responses.wallet.WalletResponse;
import com.fo_product.merchant_service.dtos.responses.wallet.WalletTransactionResponse;
import com.fo_product.merchant_service.events.WalletWithdrawalEvent;
import com.fo_product.merchant_service.exceptions.MerchantException;
import com.fo_product.merchant_service.exceptions.codes.MerchantErrorCode;
import com.fo_product.merchant_service.mappers.wallet.WalletMapper;
import com.fo_product.merchant_service.models.entities.restaurant.Restaurant;
import com.fo_product.merchant_service.models.entities.wallet.Wallet;
import com.fo_product.merchant_service.models.entities.wallet.WalletTransaction;
import com.fo_product.merchant_service.models.enums.TransactionType;
import com.fo_product.merchant_service.models.repositories.restaurant.RestaurantRepository;
import com.fo_product.merchant_service.models.repositories.wallet.WalletRepository;
import com.fo_product.merchant_service.models.repositories.wallet.WalletTransactionRepository;
import com.fo_product.merchant_service.services.interfaces.wallet.IWalletService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WalletService implements IWalletService {
    WalletRepository walletRepository;
    WalletTransactionRepository walletTransactionRepository;
    RestaurantRepository restaurantRepository;
    WalletMapper mapper;
    KafkaTemplate<String, Object> kafkaTemplate; // Injected KafkaTemplate
    ApplicationEventPublisher applicationEventPublisher;

    @Override
    @Transactional
    public void createWallet(Long restaurantId) {
        restaurantRepository.findById(restaurantId).ifPresent(restaurant -> {
            if (walletRepository.findByRestaurant_Id(restaurantId).isEmpty()) {
                walletRepository.save(Wallet.builder()
                        .restaurant(restaurant)
                        .balance(BigDecimal.ZERO)
                        .build());
            }
        });
    }

    @Override
    @Transactional
    public WalletResponse getMyWallet(Long restaurantId) {
        Wallet wallet = getMyWalletEntity(restaurantId);
        return mapper.response(wallet);
    }

    @Override
    @Transactional
    public Page<WalletTransactionResponse> getMyTransactions(Long restaurantId, int page, int size, Instant startDate, Instant endDate, TransactionType type) {
        Wallet wallet = getMyWalletEntity(restaurantId);
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        
        Specification<WalletTransaction> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("wallet"), wallet));
            
            if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), LocalDateTime.ofInstant(startDate, ZoneId.systemDefault())));
            }
            if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), LocalDateTime.ofInstant(endDate, ZoneId.systemDefault())));
            }
            if (type != null) {
                predicates.add(cb.equal(root.get("transactionType"), type));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return walletTransactionRepository.findAll(spec, pageable)
                .map(mapper::response);
    }

    @Override
    @Transactional
    public byte[] exportTransactions(Long restaurantId, Instant startDate, Instant endDate, TransactionType type) {
        // Implement simplified CSV export
        Wallet wallet = getMyWalletEntity(restaurantId);
        
        Specification<WalletTransaction> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("wallet"), wallet));
            
            if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), LocalDateTime.ofInstant(startDate, ZoneId.systemDefault())));
            }
            if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), LocalDateTime.ofInstant(endDate, ZoneId.systemDefault())));
            }
            if (type != null) {
                predicates.add(cb.equal(root.get("transactionType"), type));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        List<WalletTransaction> transactions = walletTransactionRepository.findAll(spec, Sort.by("createdAt").descending());
        
        StringBuilder csv = new StringBuilder();
        // Add BOM for Excel to recognize UTF-8
        csv.append("\uFEFF"); 
        csv.append("ID,Amount,Type,Description,Date\n");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        for (WalletTransaction tx : transactions) {
            csv.append(tx.getId()).append(",");
            csv.append(tx.getAmount()).append(",");
            csv.append(tx.getTransactionType()).append(",");
            csv.append("\"").append(tx.getDescription().replace("\"", "\"\"")).append("\",");
            csv.append(formatter.format(tx.getCreatedAt())).append("\n");
        }
        
        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DailyStatResponse> getDailyStatistics(Long restaurantId) {
        Wallet wallet = getMyWalletEntity(restaurantId);
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        
        List<Object[]> nativeStats = walletTransactionRepository.getDailyStatisticsNative(wallet.getId(), startDate);
        
        Map<LocalDate, DailyStatResponse> statMap = new HashMap<>();
        for (Object[] row : nativeStats) {
            LocalDate date = ((java.sql.Date) row[0]).toLocalDate();
            BigDecimal income = new BigDecimal(row[1].toString());
            BigDecimal expense = new BigDecimal(row[2].toString());
            statMap.put(date, DailyStatResponse.builder()
                    .date(date).income(income).expense(expense).build());
        }

        List<DailyStatResponse> response = new ArrayList<>();
        // Last 7 days
        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            response.add(statMap.getOrDefault(date, DailyStatResponse.builder()
                    .date(date).income(BigDecimal.ZERO).expense(BigDecimal.ZERO).build()));
        }
        
        return response;
    }

    @Override
    @Transactional
    public WalletResponse withdraw(Long restaurantId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new MerchantException(MerchantErrorCode.INVALID_AMOUNT);
        }

        Wallet wallet = getLockedMyWalletEntity(restaurantId);
        
        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new MerchantException(MerchantErrorCode.INSUFFICIENT_BALANCE);
        }

        // Deduct balance
        wallet.setBalance(wallet.getBalance().subtract(amount));
        walletRepository.save(wallet);

        // Create Transaction Record (Negative amount for withdrawal)
        WalletTransaction transaction = WalletTransaction.builder()
                .wallet(wallet)
                .amount(amount.negate())
                .transactionType(TransactionType.WITHDRAW)
                .description("Rút tiền về tài khoản ngân hàng")
                .build();
        
        walletTransactionRepository.save(transaction);
        
        // Publish Event locally to be processed AFTER_COMMIT
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long userId = (Long) jwt.getClaims().get("user-id");
        
        applicationEventPublisher.publishEvent(WalletWithdrawalEvent.builder()
                .userId(userId)
                .amount(amount)
                .transactionId(transaction.getId())
                .time(Instant.now())
                .build());

        return mapper.response(wallet);
    }

    @Override
    @Transactional
    public WalletResponse deposit(Long restaurantId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new MerchantException(MerchantErrorCode.INVALID_AMOUNT);
        }

        Wallet wallet = getLockedMyWalletEntity(restaurantId);
        
        // Add to balance
        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepository.save(wallet);

        // Create Transaction Record
        WalletTransaction transaction = WalletTransaction.builder()
                .wallet(wallet)
                .amount(amount) // Positive amount for deposit
                .transactionType(TransactionType.DEPOSIT)
                .description("Nạp tiền vào ví")
                .build();
        
        walletTransactionRepository.save(transaction);

        return mapper.response(wallet);
    }
    
    private Wallet getMyWalletEntity(Long restaurantId) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal(); // Cast to Jwt
        Long userId = (Long) jwt.getClaims().get("user-id"); // Get user-id claim

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new MerchantException(MerchantErrorCode.RESTAURANT_NOT_EXIST));

        if (!restaurant.getOwnerId().equals(userId)) {
            throw new MerchantException(MerchantErrorCode.UNAUTHORIZED_RESTAURANT_ACCESS);
        }

        return walletRepository.findByRestaurant_Id(restaurant.getId())
                .orElseGet(() -> {
                    log.info("Creating new wallet for restaurant {}", restaurant.getId());
                    return walletRepository.save(Wallet.builder()
                            .restaurant(restaurant)
                            .balance(BigDecimal.ZERO)
                            .build());
                });
    }

    private Wallet getLockedMyWalletEntity(Long restaurantId) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long userId = (Long) jwt.getClaims().get("user-id");

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new MerchantException(MerchantErrorCode.RESTAURANT_NOT_EXIST));

        if (!restaurant.getOwnerId().equals(userId)) {
            throw new MerchantException(MerchantErrorCode.UNAUTHORIZED_RESTAURANT_ACCESS);
        }

        return walletRepository.findLockedByRestaurant_Id(restaurant.getId())
                .orElseGet(() -> {
                    log.info("Creating new locked wallet for restaurant {}", restaurant.getId());
                    return walletRepository.save(Wallet.builder()
                            .restaurant(restaurant)
                            .balance(BigDecimal.ZERO)
                            .build());
                });
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendKafkaMessageAfterCommit(WalletWithdrawalEvent event) {
        kafkaTemplate.send("wallet-withdrawal-topic", event);
        log.info("Successfully sent WalletWithdrawalEvent to Kafka for transaction: {}", event.getTransactionId());
    }
}
