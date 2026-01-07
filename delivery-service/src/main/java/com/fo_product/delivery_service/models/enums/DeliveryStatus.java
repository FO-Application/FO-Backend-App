package com.fo_product.delivery_service.models.enums;

public enum DeliveryStatus {
    ACCEPTED,// Đang tìm tài xế (Lúc vừa nhận event OrderConfirmed)
    DELIVERING,
    COMPLETED
    ;
}
