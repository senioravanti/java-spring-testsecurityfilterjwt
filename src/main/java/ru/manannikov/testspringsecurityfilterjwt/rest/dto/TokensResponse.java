package ru.manannikov.testspringsecurityfilterjwt.rest.dto;

public record TokensResponse(
    String accessToken,
    String refreshToken,
    Long expiresIn
) {}
