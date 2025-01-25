package com.sweetme.back.auth.controller;

import com.sweetme.back.common.exception.CustomJWTException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
@Log4j2
public class CustomControllerAdvice {

    @ExceptionHandler(CustomJWTException.class)
    protected ResponseEntity<?> handleJWTException(CustomJWTException e) {

        String message = e.getMessage();

        return ResponseEntity.ok().body(Map.of("error", message));
    }
}
