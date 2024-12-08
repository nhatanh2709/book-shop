package com.example.bill_service.service;


import com.example.bill_service.dto.request.MomoPaymentRequest;
import com.example.bill_service.dto.request.PaymentClientRequest;
import com.example.bill_service.dto.request.UpdateTransactionsRequest;
import com.example.bill_service.dto.response.BillResponse;
import com.example.bill_service.dto.response.MomoPaymentResponse;
import com.example.bill_service.entity.Bill;
import com.example.bill_service.exception.AppException;
import com.example.bill_service.exception.ErrorCode;
import com.example.bill_service.mapper.BillMapper;
import com.example.bill_service.repostiory.BillRepository;
import com.example.bill_service.repostiory.httpClient.IdentityClient;
import com.example.bill_service.repostiory.httpClient.MomoPaymentClient;
import com.example.bill_service.repostiory.httpClient.UserProfileFeignClient;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BillServiceMomoPayment {
    BillRepository billRepository;
    BillMapper billMapper;
    @Value("${momo.accessKey}")
    @NonFinal
    String accessKey;

    @Value("${momo.secretKey}")
    @NonFinal
    String secretKey;

    @Value("${momo.redirectUrl}")
    @NonFinal
    String RedirectUrl;

    @Value("${momo.ipnUrl}")
    @NonFinal
    String IpnUrl;

    IdentityClient identityClient;
    UserProfileFeignClient userProfileFeignClient;


    public JsonNode createPayment(PaymentClientRequest paymentClientRequest) throws Exception{
        Bill bill = billRepository.findByBillId(paymentClientRequest.getPackageId()).orElseThrow(() ->
                new AppException(ErrorCode.BILL_NOT_FOUND)
                );
        String partnerCode = "MOMO";
        String orderInfo = "pay with MoMo";
        String redirectUrl = RedirectUrl;
        String ipnUrl = IpnUrl;
        String amount = bill.getPrice().toString();
        String orderId = partnerCode + System.currentTimeMillis();
        String requestId = orderId;
        log.info(orderId);
        // Create raw signature
        String username = identityClient.getMyInfo().getResult().getEmail();
        String rawSignature = String.format(
                "accessKey=%s&amount=%s&extraData=%s&ipnUrl=%s&orderId=%s&orderInfo=%s&partnerCode=%s&redirectUrl=%s&requestId=%s&requestType=payWithMethod",
                accessKey, amount,username, ipnUrl, orderId, orderInfo, partnerCode, redirectUrl, requestId
        );
        log.info("rawSignature: {}", rawSignature);
        String signature =generateSignature(rawSignature, secretKey);
        log.info("Signature: {}", signature);

        MomoPaymentRequest request = MomoPaymentRequest.builder()
                .partnerCode(partnerCode)
                .partnerName("Test")
                .storeId("MomoTestStore")
                .requestId(requestId)
                .amount(amount)
                .orderId(orderId)
                .orderInfo(orderInfo)
                .redirectUrl(redirectUrl)
                .ipnUrl(ipnUrl)
                .lang("vi")
                .requestType("payWithMethod")
                .autoCapture(true)
                .extraData(username)
                .orderGroupId("")
                .signature(signature)
                .build();

        String jsonRequest = new ObjectMapper()
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .writeValueAsString(request);



        // Create HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.setContentLength(jsonRequest.getBytes(StandardCharsets.UTF_8).length);

        // Create HTTP entity with body and headers
        HttpEntity<String> entity = new HttpEntity<>(jsonRequest, headers);

        // Define the URL
        String url = "https://test-payment.momo.vn/v2/gateway/api/create";

        // Send the POST request
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.POST, entity, JsonNode.class);
            JsonNode responseBody = response.getBody();
            return responseBody;
        } catch (Exception e) {
            throw new AppException(ErrorCode.CREATE_TRANSACTIONS_FAILED);
        }


    }

    private String generateSignature(String data, String key) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256_HMAC.init(secret_key);

        byte[] hash = sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(hash);
    }

    private String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public void callbackData(String email) {
        UpdateTransactionsRequest request = UpdateTransactionsRequest.builder().email(email).build();
        userProfileFeignClient.updateTransactions(request);

    }

    public List<BillResponse> getAllBill() {
        return billRepository.findAll().stream().map(billMapper::toBillResponse).toList();
    }

    public BillResponse getBillByID(String ID) {
        Bill bill= billRepository.findByBillId(ID).orElseThrow(() ->
                new AppException(ErrorCode.BILL_NOT_FOUND)
                );
        return billMapper.toBillResponse(bill);
    }

}
