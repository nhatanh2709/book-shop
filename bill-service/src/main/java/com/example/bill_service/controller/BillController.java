package com.example.bill_service.controller;

import com.example.bill_service.dto.ApiResponse;
import com.example.bill_service.dto.request.CreateBillRequest;
import com.example.bill_service.dto.request.PaymentClientRequest;
import com.example.bill_service.dto.response.BillResponse;
import com.example.bill_service.dto.response.MomoPaymentResponse;
import com.example.bill_service.service.BillServiceMomoPayment;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class BillController {

    BillServiceMomoPayment billServiceMomoPayment;
    @GetMapping("/getAll")
    ApiResponse<List<BillResponse>> getAll() {
        return ApiResponse.<List<BillResponse>>builder()
                .result(billServiceMomoPayment.getAllBill())
                .build();
    }

    @GetMapping("/get/{id}")
    public ApiResponse<BillResponse> getBillByID(@PathVariable("id") String id) {
        return ApiResponse.<BillResponse>builder()
                .result(billServiceMomoPayment.getBillByID(id))
                .build();
    }


    @PostMapping("/transactions")
    ApiResponse<JsonNode>createPayment(@RequestBody PaymentClientRequest request) throws Exception {
        return ApiResponse.<JsonNode>builder()
                .result(billServiceMomoPayment.createPayment(request))
                .build();
    }

    @PostMapping("/callback")
    ApiResponse<Void>callbackMomo(@RequestBody MomoPaymentResponse request) throws Exception {
        billServiceMomoPayment.callbackData(request.getExtraData());
        return ApiResponse.<Void>builder().build();
    }

}
