package hotel_booking.service;

import hotel_booking.config.VNPayConfig;
import hotel_booking.entity.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class VNPayService {

    private final VNPayConfig config;

    public String createPaymentUrl(Payment payment) {
        String txnRef = payment.getTransactionReference();
        String amount = payment.getAmount().multiply(BigDecimal.valueOf(100)).toBigInteger().toString();
        Map<String, String> params = new HashMap<>();
        params.put("vnp_Version", config.getVersion());
        params.put("vnp_Command", config.getCommand());
        params.put("vnp_TmnCode", config.getTmnCode());
        params.put("vnp_Amount", amount);
        params.put("vnp_CurrCode", config.getCurrCode());
        params.put("vnp_TxnRef", txnRef);
        params.put("vnp_OrderInfo", "Thanh toan booking " + payment.getBooking().getId());
        params.put("vnp_OrderType", "other");
        params.put("vnp_Locale", config.getLocale());
        params.put("vnp_ReturnUrl", config.getReturnUrl());
        params.put("vnp_IpAddr", "127.0.0.1");
        String createDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        params.put("vnp_CreateDate", createDate);
        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        for (String fieldName : fieldNames) {
            String value = params.get(fieldName);
            if (value != null && !value.isEmpty()) {
                // 1. Build Hash Data using RAW value (NOT URL encoded)
                hashData.append(fieldName).append("=").append(value);
                hashData.append("&");

                // 2. Build Query using URL encoded value
                String encodedValue = URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20");
                query.append(fieldName).append("=").append(encodedValue);
                query.append("&");
            }
        }
        hashData.deleteCharAt(hashData.length() - 1);
        query.deleteCharAt(query.length() - 1);
        System.out.println("=== VNPAY HASH DATA (RAW): " + hashData.toString());
        String secureHash = hmacSHA512(config.getHashSecret(), hashData.toString());
        System.out.println("=== VNPAY SECURE HASH: " + secureHash);
        String finalUrl = config.getPayUrl() + "?" + query + "&vnp_SecureHash=" + secureHash;
        System.out.println("=== VNPAY GENERATED URL: " + finalUrl);
        return finalUrl;
    }

    public boolean validateSignature(Map<String, String> params) {
        String vnpSecureHash = params.get("vnp_SecureHash");
        Map<String, String> filtered = new HashMap<>(params);
        filtered.remove("vnp_SecureHash");
        filtered.remove("vnp_SecureHashType");
        List<String> fieldNames = new ArrayList<>(filtered.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        for (String fieldName : fieldNames) {
            String value = filtered.get(fieldName);
            if (value != null && !value.isEmpty()) {
                // Use RAW value (NOT URL encoded)
                hashData.append(fieldName).append("=").append(value);
                hashData.append("&");
            }
        }

        if (hashData.length() > 0) {
            hashData.deleteCharAt(hashData.length() - 1);
        }
        System.out.println("=== VNPAY RETURN HASH DATA (RAW): " + hashData.toString());
        String calculatedHash = hmacSHA512(config.getHashSecret(), hashData.toString());
        System.out.println("=== CALCULATED SECURE HASH: " + calculatedHash);
        System.out.println("=== VNPAY SECURE HASH FROM PARAMS: " + vnpSecureHash);
        return calculatedHash.equalsIgnoreCase(vnpSecureHash);
    }

    public String hmacSHA512(String key, String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            mac.init(secretKey);
            byte[] rawHmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(2 * rawHmac.length);
            for (byte b : rawHmac) {
                String hexStr = Integer.toHexString(0xff & b);
                if (hexStr.length() == 1) {hex.append('0');}
                hex.append(hexStr);
            }
            return hex.toString();
        } catch (Exception ex) {
            throw new RuntimeException("Error while hashing", ex);
        }
    }

    public String getReturnUrl() {
        return config.getReturnUrl();
    }
}