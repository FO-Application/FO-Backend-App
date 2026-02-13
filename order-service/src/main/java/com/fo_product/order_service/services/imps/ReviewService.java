package com.fo_product.order_service.services.imps;

import com.fo_product.order_service.dtos.requests.ReviewRequest;
import com.fo_product.order_service.dtos.responses.ReviewResponse;
import com.fo_product.order_service.dtos.feigns.UserDTO;
import com.fo_product.order_service.exceptions.OrderException;
import com.fo_product.order_service.exceptions.ReviewException;
import com.fo_product.order_service.exceptions.codes.OrderErrorCode;
import com.fo_product.order_service.exceptions.codes.ReviewErrorCode;
import com.fo_product.order_service.helpers.GetClientDTO;
import com.fo_product.order_service.kafka.KafkaProducerService;
import com.fo_product.order_service.kafka.events.ReviewCreatedEvent;
import com.fo_product.order_service.mappers.ReviewMapper;
import com.fo_product.order_service.models.entities.Order;
import com.fo_product.order_service.models.entities.Review;
import com.fo_product.order_service.models.enums.OrderStatus;
import com.fo_product.order_service.models.repositories.OrderRepository;
import com.fo_product.order_service.models.repositories.ReviewRepository;
import com.fo_product.order_service.services.interfaces.IReviewService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReviewService implements IReviewService {
    OrderRepository orderRepository;
    ReviewRepository reviewRepository;
    ReviewMapper mapper;
    KafkaProducerService kafkaProducerService;
    GetClientDTO getClientDTO;

    private String getUserName(Long userId) {
        try {
            UserDTO user = getClientDTO.getUserDTO(userId);
            if (user != null) {
                String name = (user.firstName() != null ? user.firstName() : "") +
                        (user.lastName() != null ? " " + user.lastName() : "");
                return name.trim().isEmpty() ? "Khách hàng" : name.trim();
            }
        } catch (Exception e) {
            // Fallback if user-service is unavailable
        }
        return "Khách hàng";
    }

    @Override
    @Transactional
    public ReviewResponse createReview(Long userId, ReviewRequest request) {
        Order order = orderRepository.findById(request.orderId())
                .orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_NOT_EXIST));

        if (!order.getUserId().equals(userId))
            throw new OrderException(OrderErrorCode.ORDER_INVALID);

        if (order.getOrderStatus() != OrderStatus.COMPLETED)
            throw new OrderException(OrderErrorCode.UNFINISHED_ORDER);

        if (reviewRepository.existsByOrder_Id(order.getId()))
            throw new ReviewException(ReviewErrorCode.ORDER_ALREADY_REVIEWED);

        Review review = Review.builder()
                .order(order)
                .userId(userId)
                .merchantId(order.getMerchantId())
                .rating(request.rating())
                .comment(request.comment())
                .build();

        Review result = reviewRepository.save(review);

        ReviewCreatedEvent reviewCreatedEvent = ReviewCreatedEvent.builder()
                .merchantId(review.getMerchantId())
                .rating(review.getRating())
                .build();

        kafkaProducerService.sendReviewCreatedEvent(reviewCreatedEvent);

        return mapper.response(result, getUserName(userId));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewResponse> getReviewsByMerchant(Long merchantId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<Review> reviews = reviewRepository.findByMerchantId(merchantId, pageable);

        return reviews.map(review -> mapper.response(review, getUserName(review.getUserId())));
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewResponse getReviewByOrder(Long orderId) {
        Review review = reviewRepository.findByOrder_Id(orderId)
                .orElseThrow(() -> new ReviewException(ReviewErrorCode.REVIEW_NOT_EXIST));

        return mapper.response(review, getUserName(review.getUserId()));
    }
}

