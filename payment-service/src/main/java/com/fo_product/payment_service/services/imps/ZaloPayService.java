package com.fo_product.payment_service.services.imps;

import com.fo_product.payment_service.clients.OrderClient;
import com.fo_product.payment_service.configs.ZaloPayConfig;
import com.fo_product.payment_service.exceptions.PaymentException;
import com.fo_product.payment_service.exceptions.codes.PaymentErrorCode;
import com.fo_product.payment_service.services.interfaces.IZaloPayService;
import com.fo_product.payment_service.utils.HmacUtil;
import jakarta.xml.bind.DatatypeConverter;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.net.URIBuilder;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ZaloPayService implements IZaloPayService {
    ZaloPayConfig zaloPayConfig;
    OrderClient orderClient;

    @Override
    public Map<String, Object> createOrder(Long orderId, long amount) throws Exception {
        String appTransId = getCurrentTimeString("yyMMdd") +"_"+ new Date().getTime();

        try {
            orderClient.updateAppTransId(orderId, appTransId);
            log.info("Linked Order {} with AppTransId {}", orderId, appTransId);
        } catch (Exception e) {
            log.error("Failed to link order with transId", e);
            throw new PaymentException(PaymentErrorCode.CANT_LINK_UP_ORDER_WITH_PAYMENT_SERVICE);
        }

        Map<String, Object> order = new HashMap<String, Object>() {{
            put("app_id", zaloPayConfig.getAppId());
            put("app_trans_id", appTransId);
            put("app_time", System.currentTimeMillis());
            put("app_user", "FastBite");
            put("amount", amount);
            put("description", "Thanh toan don hang #" + orderId);
            put("bank_code", "");
            put("item", "[]");
            put("embed_data", "{\"redirecturl\": \"https://google.com\"}"); // Link mở lại app sau khi thanh toán
            put("callback_url", zaloPayConfig.getCallbackUrl());
            put("expire_duration_seconds", 900); //Secconds
        }};

        String data = order.get("app_id") + "|" + order.get("app_trans_id") + "|" + order.get("app_user") + "|" + order.get("amount")
                + "|" + order.get("app_time") + "|" + order.get("embed_data") + "|" + order.get("item");
        order.put("mac", HmacUtil.HMacHexStringEncode(HmacUtil.HMACSHA256, zaloPayConfig.getKey1(), data));

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(zaloPayConfig.getCreateOrderEndpoint());

        List<NameValuePair> params = new ArrayList<>();
        for (Map.Entry<String, Object> e : order.entrySet()) {
            params.add(new BasicNameValuePair(e.getKey(), e.getValue().toString()));
        }

        post.setEntity(new UrlEncodedFormEntity(params));

        CloseableHttpResponse res = client.execute(post);
        BufferedReader rd = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
        StringBuilder resultJsonStr = new StringBuilder();
        String line;

        while ((line = rd.readLine()) != null) {
            resultJsonStr.append(line);
        }

        JSONObject jsonResult = new JSONObject(resultJsonStr.toString());
        Map<String, Object> finalResult = new HashMap<>();
        for (Iterator it = jsonResult.keys(); it.hasNext(); ) {

            String key = (String) it.next();
            finalResult.put(key, jsonResult.get(key));
        }

        // Add app_trans_id to the response so frontend can use it for polling payment status
        finalResult.put("app_trans_id", appTransId);

        return finalResult;
    }

    @Override
    public Object callBack(JSONObject result, String jsonString) throws Exception{
        Mac HmacSHA256 = Mac.getInstance("HmacSHA256");
        HmacSHA256.init(new SecretKeySpec(zaloPayConfig.getKey2().getBytes(), "HmacSHA256"));

        try {
            JSONObject cbdata = new JSONObject(jsonString);
            String dataStr = cbdata.getString("data");
            String reqMac = cbdata.getString("mac");

            byte[] hashBytes = HmacSHA256.doFinal(dataStr.getBytes());
            String mac = DatatypeConverter.printHexBinary(hashBytes).toLowerCase();

            // check if the callback is valid (from ZaloPay server)
            if (!reqMac.equals(mac)) {
                // callback is invalid
                result.put("return_code", -1);
                result.put("return_message", "mac not equal");
            } else {
                // payment success
                // merchant update status for order's status
                JSONObject data = new JSONObject(dataStr);
                log.info("update order's status = success where app_trans_id = " + data.getString("app_trans_id"));

                result.put("return_code", 1);
                result.put("return_message", "success");
            }
        } catch (Exception ex) {
            result.put("return_code", 0); // callback again (up to 3 times)
            result.put("return_message", ex.getMessage());
        }

        return result;
    }

    @Override
    public Map<String, Object> queryOrder(String appTransId) throws Exception{
        String data = zaloPayConfig.getAppId() +"|"+ appTransId  +"|"+ zaloPayConfig.getKey1(); // appid|app_trans_id|key1
        String mac = HmacUtil.HMacHexStringEncode(HmacUtil.HMACSHA256, zaloPayConfig.getKey1(), data);

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("app_id", zaloPayConfig.getAppId()));
        params.add(new BasicNameValuePair("app_trans_id", appTransId));
        params.add(new BasicNameValuePair("mac", mac));

        URIBuilder uri = new URIBuilder(zaloPayConfig.getQueryEndpoint());
        uri.addParameters(params);

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(uri.build());
        post.setEntity(new UrlEncodedFormEntity(params));

        CloseableHttpResponse res = client.execute(post);
        BufferedReader rd = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
        StringBuilder resultJsonStr = new StringBuilder();
        String line;

        while ((line = rd.readLine()) != null) {
            resultJsonStr.append(line);
        }

        JSONObject result = new JSONObject(resultJsonStr.toString());
        Map<String, Object> finalResult = new HashMap<>();
        if(result.has("return_code")){
            int returnCode = result.getInt("return_code");
            finalResult.put("return_code", result.get("return_code"));
            finalResult.put("return_message", result.get("return_message"));
            finalResult.put("is_processing", result.get("is_processing"));
            finalResult.put("amount", result.get("amount"));
            finalResult.put("zp_trans_id", result.opt("zp_trans_id"));

            if (returnCode == 1) {
                // Gọi sang Order Service để update trạng thái thành PAID
                log.info("Query thấy đơn {} thành công. Đang gọi Order Service update...", appTransId);
                orderClient.updateOrderStatus(appTransId, "PAID");
                finalResult.put("order_status_update", "SUCCESS");
            }
        } else {
            finalResult.put("return_code", -1);
            finalResult.put("return_message", "Unknown error or Parsing error");
        }
        return finalResult;
    }

    private String getCurrentTimeString(String format) {
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT+7"));
        SimpleDateFormat fmt = new SimpleDateFormat(format);
        fmt.setCalendar(cal);
        return fmt.format(cal.getTimeInMillis());
    }
}