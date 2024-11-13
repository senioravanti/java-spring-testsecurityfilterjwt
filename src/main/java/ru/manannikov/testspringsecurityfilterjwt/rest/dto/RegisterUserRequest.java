package ru.manannikov.testspringsecurityfilterjwt.rest.dto;

import java.util.List;

public record RegisterUserRequest(
    String username,
    String password,
    List<String> authorities
) {}
