package com.example.bill_service.repostiory.httpClient;

import com.example.bill_service.dto.request.MomoPaymentRequest;
import com.example.bill_service.dto.response.MomoPaymentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "momoPaymentClient", url = "https://test-payment.momo.vn/v2/gateway/api")
public interface MomoPaymentClient {
    @PostMapping("/create")
    MomoPaymentResponse createPayment(
            @RequestHeader("Content-Type") String contentType,
            @RequestBody String request
    );
}