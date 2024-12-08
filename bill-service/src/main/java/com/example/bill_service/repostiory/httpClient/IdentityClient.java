package com.example.bill_service.repostiory.httpClient;


import com.example.bill_service.configuration.AuthenticationRequestInterceptor;
import com.example.bill_service.dto.ApiResponse;
import com.example.bill_service.dto.response.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(
        name = "identity-service",
        url = "${app.services.identity}",
        configuration = {AuthenticationRequestInterceptor.class})
public interface IdentityClient {
    @GetMapping(value = "/users/myInfo")
    ApiResponse<UserResponse> getMyInfo();
}


