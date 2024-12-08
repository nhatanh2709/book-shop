package com.example.bill_service.repostiory.httpClient;

import com.example.bill_service.dto.ApiResponse;
import com.example.bill_service.dto.request.UpdateTransactionsRequest;
import com.example.bill_service.dto.response.UserProfileResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "userProfileClient", url = "${app.services.profile}")
public interface UserProfileFeignClient {

    @PutMapping("/users/transactions")
    ApiResponse<UserProfileResponse> updateTransactions(@RequestBody UpdateTransactionsRequest email);
}

