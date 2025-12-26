package com.fo_product.order_service.mappers;

import com.fo_product.order_service.dtos.responses.ReviewResponse;
import com.fo_product.order_service.models.entities.Review;
import org.springframework.stereotype.Component;

@Component
public class ReviewMapper {
    public ReviewResponse response (Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .userId(review.getUserId())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
