package com.example.notification_services.controller;

import com.example.notification_services.dto.ApiResponse;
import com.example.notification_services.dto.request.SendEmailRequest;
import com.example.notification_services.dto.response.EmailResponse;
import com.example.notification_services.service.EmailService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailController {
    EmailService emailService;

    @PostMapping("/email/send")
    ApiResponse<EmailResponse> sendEmail(@RequestBody SendEmailRequest request) {
        return ApiResponse.<EmailResponse>builder()
                .result(emailService.sendEmail(request))
                .build();
    }


    @KafkaListener(topics = "onboard-successful")
    public void listen(String message) {
        log.info("Message received: {}", message);
    }
}