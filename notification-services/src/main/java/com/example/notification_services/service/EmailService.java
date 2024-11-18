package com.example.notification_services.service;

import com.example.notification_services.dto.request.EmailRequest;
import com.example.notification_services.dto.request.SendEmailRequest;
import com.example.notification_services.dto.request.Sender;
import com.example.notification_services.dto.response.EmailResponse;
import com.example.notification_services.exception.AppException;
import com.example.notification_services.exception.ErrorCode;
import com.example.notification_services.repository.httpClient.EmailClient;
import feign.FeignException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class EmailService {

    EmailClient emailClient;
    @Value("${notification.email.brevo-apikey}")
    @NonFinal
    String apiKey;

    String htmlContent = "<!DOCTYPE html> <html lang=\"en\"> <head>     <meta charset=\"UTF-8\">     <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">     <title>Email Template</title>     <style>         /* Thiết lập các thuộc tính cơ bản cho email */         body {             font-family: Arial, sans-serif;             margin: 0;             padding: 0;             background-color: #f4f4f4;         }         .email-container {             max-width: 600px;             margin: 20px auto;             background-color: #ffffff;             padding: 20px;             border: 1px solid #dddddd;         }         .header {             background-color: #4CAF50;             color: white;             padding: 10px;             text-align: center;         }         .content {             margin: 20px 0;             line-height: 1.6;             color: #333333;         }         .button {             display: inline-block;             background-color: #4CAF50;             color: white;             padding: 10px 15px;             text-decoration: none;             border-radius: 5px;             font-weight: bold;         }         .footer {             font-size: 12px;             color: #777777;             text-align: center;             margin-top: 20px;             border-top: 1px solid #dddddd;             padding-top: 10px;         }     </style> </head> <body>     <div class=\"email-container\">         <!-- Header -->         <div class=\"header\">             <h1>Welcome to Our Service</h1>         </div>          <!-- Main Content -->         <div class=\"content\">             <p>Welcome to movie web </p>             <p>Thank you for signing up for our service. We are thrilled to have you on board! Here are some details to get you started:</p>         </div>          <!-- Footer -->         <div class=\"footer\">             <p>&copy; 2024 Our Company. All rights reserved.</p>             <p>If you have any questions, contact us at support alonhuan123@gmail.com</p>         </div>     </div> </body> </html>";

    public EmailResponse sendEmail(SendEmailRequest request) {
        log.info("Started Request: {}", request);
        EmailRequest emailRequest = EmailRequest.builder()
                .sender(Sender.builder()
                        .name("Welcome to my app")
                        .email("alonhuan123@gmail.com")
                        .build())
                .to(List.of(request.getTo()))
                .subject(request.getSubject())
                .htmlContent(htmlContent)
                .build();
        log.info("Email Request: {}", emailRequest);
        try {
            return emailClient.sendEmail(apiKey, emailRequest);
        } catch (FeignException e) {
            throw new AppException(ErrorCode.CANNOT_SEND_MAIL);
        }
    }
}
