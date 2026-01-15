package com.fo_product.merchant_service.kafka;

import com.fo_product.merchant_service.exceptions.MerchantException;
import com.fo_product.merchant_service.exceptions.codes.MerchantErrorCode;
import com.fo_product.merchant_service.kafka.events.OrderCompletedEvent;
import com.fo_product.merchant_service.kafka.events.ReviewCreatedEvent;
import com.fo_product.merchant_service.models.entities.restaurant.Restaurant;
import com.fo_product.merchant_service.models.entities.wallet.Wallet;
import com.fo_product.merchant_service.models.entities.wallet.WalletTransaction;
import com.fo_product.merchant_service.models.enums.TransactionType;
import com.fo_product.merchant_service.models.repositories.restaurant.RestaurantRepository;
import com.fo_product.merchant_service.models.repositories.wallet.WalletRepository;
import com.fo_product.merchant_service.models.repositories.wallet.WalletTransactionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class KafkaConsumerService {
    WalletRepository walletRepository;
    RestaurantRepository restaurantRepository;
    WalletTransactionRepository walletTransactionRepository;

    @KafkaListener(topics = "order-completed-topic", groupId = "merchant-service-group")
    @Transactional
    public void handleOrderCompleted(OrderCompletedEvent event) {
        log.info("Merchant Service: Nhận tiền từ đơn hàng {}", event.orderId());

        Wallet wallet = walletRepository.findByRestaurant_Id(event.merchantId())
                .orElseGet(() -> {
                    Restaurant restaurant = restaurantRepository.findById(event.merchantId()).orElseThrow(() -> new MerchantException(MerchantErrorCode.RESTAURANT_NOT_EXIST));

                    return walletRepository.save(Wallet.builder()
                                    .restaurant(restaurant)
                                    .balance(BigDecimal.ZERO)
                            .build());
                });
        BigDecimal amount = event.orderAmount();
        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepository.save(wallet);

        WalletTransaction walletTransaction = WalletTransaction.builder()
                .wallet(wallet)
                .amount(amount)
                .transactionType(TransactionType.ORDER_INCOME)
                .orderId(event.orderId())
                .description("Thu nhập từ đơn hàng #" + event.orderId())
                .build();

        walletTransactionRepository.save(walletTransaction);
        log.info("Đã cộng {} vào ví ID {}", amount, wallet.getId());
    }

    @KafkaListener(topics = "review-created-topic", groupId = "merchant-service-group")
    @Transactional
    public void handleReviewCreated(ReviewCreatedEvent event) {
        log.info("Nhận rating mới: {} sao cho quán ID {}", event.getRating(), event.getMerchantId());
        Restaurant restaurant = restaurantRepository.findById(event.getMerchantId())
                .orElseThrow(() -> new MerchantException(MerchantErrorCode.RESTAURANT_NOT_EXIST));

        //Lấy giá trị hiện tại
        double currentAvg = restaurant.getRatingAverage() == null ? 0.0 : restaurant.getRatingAverage();
        int currentCount = restaurant.getReviewCount();

        //Tính toán điểm mới (Công thức trung bình cộng dồn)
        //(Diểm cữ * Số lượng cũ + điểm mới) / (Số lượng cũ + 1)
        double newTotalScore = (currentAvg * currentCount) + event.getRating();
        int newCount = currentCount + 1;

        double newAvg = newTotalScore / newCount;
        //Làm tròn 1 chữ số thập phân
        newAvg = Math.round(newAvg * 10.0) / 10.0;

        restaurant.setRatingAverage(newAvg);
        restaurant.setReviewCount(newCount);

        restaurantRepository.save(restaurant);
        log.info("Đã cập nhật quán {}: Avg {} -> {}, Count {} -> {}",
                restaurant.getId(), currentAvg, newAvg, currentCount, newCount);

    }
}