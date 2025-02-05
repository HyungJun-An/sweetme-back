package com.sweetme.back.profile.domain;

import org.springframework.context.annotation.Configuration;

@Configuration
public class ProfileConstants {

    public static final int MAX_DESCRIPTION_LENGTH = 1000; // 소개 최대 길이
    public static final int MAX_URL_LENGTH = 1024; // URL 최대 길이
    public static final String URL_PATTERN = "^(https?://)?([\\da-z.-]+)\\.([a-z.]{2,6})[/\\w .-]*/?$";
}
