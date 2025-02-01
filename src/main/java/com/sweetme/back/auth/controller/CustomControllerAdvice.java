package com.sweetme.back.auth.controller;

import com.sweetme.back.common.exception.CustomJWTException;
import com.sweetme.back.common.exception.SocialLoginException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Log4j2
public class CustomControllerAdvice {

    @ExceptionHandler(CustomJWTException.class)
    protected ResponseEntity<?> handleJWTException(CustomJWTException e) {

        String message = e.getMessage();

        return ResponseEntity.ok().body(Map.of("error", message));
    }

    @ExceptionHandler(SocialLoginException.class)
    public ResponseEntity<Map<String, Object>> handleSocialLoginException(SocialLoginException e) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "INVALID_SOCIAL_LOGIN");
        response.put("message", e.getMessage());
        response.put("existingLoginType", e.getExistingLoginType());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }
}
