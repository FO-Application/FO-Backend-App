package com.fo_product.order_service.services.interfaces;

import com.fo_product.order_service.dtos.requests.ReviewRequest;
import com.fo_product.order_service.dtos.responses.ReviewResponse;
import org.springframework.data.domain.Page;

public interface IReviewService {
    ReviewResponse createReview(Long userId, ReviewRequest request);
    Page<ReviewResponse> getReviewsByMerchant(Long merchantId, int page, int size);
    ReviewResponse getReviewByOrder(Long orderId);
}
