package com.sweetme.back.profile.controller;

import com.sweetme.back.common.exception.ProfileException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.util.Map;

@RestControllerAdvice
@Log4j2
public class ProfileControllerAdvice {

    @ExceptionHandler(ProfileException.class)
    protected ResponseEntity<?> handleProfileException(ProfileException e) {

        String message = e.getMessage();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", message));
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    protected ResponseEntity<?> handleAccessDeniedException(AccessDeniedException e) {

        String message = e.getMessage();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", message));
    }

}
