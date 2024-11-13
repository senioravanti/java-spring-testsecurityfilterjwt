package ru.manannikov.testspringsecurityfilterjwt.rest.dto;

public record AccessTokenRequest(
    String username,
    String password
) {}
