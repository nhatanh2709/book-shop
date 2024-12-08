package com.example.identity_service.aspect;


import com.example.identity_service.exception.AppException;
import com.example.identity_service.exception.ErrorCode;
import com.example.identity_service.service.googleCaptcha.CaptchaValidator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CapchaAspect {
    @Autowired
    CaptchaValidator captchaValidator;

    static final String CAPTCHA_HEADER_NAME = "captcha-response";

    @Around("@annotation(RequiresCaptcha)")
    public Object validateCaptcha(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String captchaResponse = request.getHeader(CAPTCHA_HEADER_NAME);
        boolean isValidCaptcha = captchaValidator.validateCaptcha(captchaResponse);
        if(!isValidCaptcha){
            throw new AppException(ErrorCode.GOOGLE_CAPTCHA_INVALID);
        }
        return joinPoint.proceed();
    }
}
