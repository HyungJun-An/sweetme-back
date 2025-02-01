package com.sweetme.back.common.exception;

import com.sweetme.back.auth.domain.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SocialLoginException extends RuntimeException {

    private final User.LoginType existingLoginType;
    private final String message;
}
