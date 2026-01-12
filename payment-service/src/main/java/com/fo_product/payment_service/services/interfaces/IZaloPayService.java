package com.fo_product.payment_service.services.interfaces;

import org.json.JSONObject;

import java.util.Map;

public interface IZaloPayService {
    Map<String, Object> createOrder(Long orderId, long amount) throws Exception;
    Object callBack(JSONObject result, String jsonStr) throws Exception;
    Map<String, Object> queryOrder(String appTransId) throws Exception;
}
