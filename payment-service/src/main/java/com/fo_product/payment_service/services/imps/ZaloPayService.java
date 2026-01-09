package com.fo_product.payment_service.services.imps;

import com.fo_product.payment_service.configs.ZaloPayConfig;
import com.fo_product.payment_service.utils.HmacUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ZaloPayService {
    ZaloPayConfig zaloPayConfig;

    public Map<String, Object> createOrder(Long orderId, long amount) throws Exception {
        String randomId = new Random().nextInt(1000000) + "";
        // Mã giao dịch bắt buộc phải có ngày tháng theo format yyMMdd
        String appTransId = getCurrentHexString("yyMMdd") + "_" + randomId;

        // 1. Tạo dữ liệu đơn hàng
        Map<String, Object> order = new HashMap<String, Object>() {{
            put("app_id", zaloPayConfig.getAppId());
            put("app_trans_id", appTransId);
            put("app_time", System.currentTimeMillis());
            put("app_user", "FO_Food_User");
            put("amount", amount);
            put("description", "Thanh toan don hang #" + orderId);
            put("bank_code", "");
            put("item", "[]");
            put("embed_data", "{\"redirecturl\": \"https://google.com\"}"); // Link mở lại app sau khi thanh toán
            put("callback_url", "https://vanessa-unabsolved-buck.ngrok-free.dev/api/v1/payment/callback");
        }};

        // 2. Tạo chữ ký (Mac)
        // ZaloPay quy định thứ tự chuỗi ký: app_id|app_trans_id|app_user|amount|app_time|embed_data|item
        String data = order.get("app_id") + "|" + order.get("app_trans_id") + "|" + order.get("app_user") + "|" + order.get("amount")
                + "|" + order.get("app_time") + "|" + order.get("embed_data") + "|" + order.get("item");

        order.put("mac", HmacUtil.encode(zaloPayConfig.getKey1(), data));

        // 3. Gửi Request sang ZaloPay
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(zaloPayConfig.getCreateOrderEndpoint());

        StringEntity entity = new StringEntity(new JSONObject(order).toString(), ContentType.APPLICATION_JSON);
        httpPost.setEntity(entity);

        CloseableHttpResponse response = httpClient.execute(httpPost);
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        StringBuilder resultJsonStr = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            resultJsonStr.append(line);
        }

        // 4. Xử lý kết quả trả về
        JSONObject result = new JSONObject(resultJsonStr.toString());
        Map<String, Object> finalResult = new HashMap<>();

        // Nếu thành công (return_code = 1) thì trả về order_url
        if (result.has("order_url")) {
            finalResult.put("order_url", result.get("order_url"));
            finalResult.put("zp_trans_token", result.get("zp_trans_token"));
            finalResult.put("return_code", result.get("return_code"));
            finalResult.put("return_message", result.get("return_message"));
        } else {
            finalResult.put("return_code", result.get("return_code"));
            finalResult.put("return_message", result.get("return_message"));
        }

        return finalResult;
    }

    private String getCurrentHexString(String format) {
        return new SimpleDateFormat(format).format(new Date());
    }
}