package com.example.bill_service.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MomoPaymentRequest {
    private String partnerCode;
    private String partnerName;
    private String storeId;
    private String requestId;
    private String amount;
    private String orderId;
    private String orderInfo;
    private String redirectUrl;
    private String ipnUrl;
    private String lang;
    private String requestType;
    private boolean autoCapture;
    private String extraData;
    private String orderGroupId;
    private String signature;
}
