package com.example.identity_service.service.googleCaptcha;

import com.example.identity_service.dto.response.CaptchaResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CaptchaValidator {
    Logger logger = LoggerFactory.getLogger(CaptchaValidator.class);

    @NonFinal
    String GOOGLE_RECAPTCHA_ENDPOINT = "https://www.google.com/recaptcha/api/siteverify";

    @NonFinal
    @Value("${google.recaptcha.secret}")
    String recaptchaSecret;

    public boolean validateCaptcha(String captchaResponse) {
        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String , String> requestMap = new LinkedMultiValueMap<>();
        requestMap.add("secret", recaptchaSecret);
        requestMap.add("response", captchaResponse);
        CaptchaResponse apiResponse = restTemplate.postForObject(GOOGLE_RECAPTCHA_ENDPOINT, requestMap, CaptchaResponse.class);
        logger.info("Captcha api response: {}", apiResponse);
        if(apiResponse == null) {
            return false;
        }

        return Boolean.TRUE.equals(apiResponse.getSuccess());
    }
}
