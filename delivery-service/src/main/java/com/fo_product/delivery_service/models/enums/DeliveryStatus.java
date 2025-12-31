package com.fo_product.delivery_service.models.enums;

public enum DeliveryStatus {
    SEARCHING,  // Đang tìm tài xế (Lúc vừa nhận event OrderConfirmed)
    ASSIGNED,   // Đã tìm thấy tài xế
    PICKED_UP,  // Tài xế đã lấy món
    DELIVERED,  // Giao thành công
    CANCELLED   // Bị hủy
    ;
}
