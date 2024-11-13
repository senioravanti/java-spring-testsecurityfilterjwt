package ru.manannikov.testspringsecurityfilterjwt.rest.controller;

import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.manannikov.testspringsecurityfilterjwt.rest.dto.AccessTokenRequest;
import ru.manannikov.testspringsecurityfilterjwt.rest.dto.RefreshTokenRequest;
import ru.manannikov.testspringsecurityfilterjwt.rest.dto.RegisterUserRequest;
import ru.manannikov.testspringsecurityfilterjwt.rest.dto.TokensResponse;
import ru.manannikov.testspringsecurityfilterjwt.services.impl.AccessTokenServiceImpl;
import ru.manannikov.testspringsecurityfilterjwt.services.impl.TokensServiceImpl;
import ru.manannikov.testspringsecurityfilterjwt.services.impl.UserDetailsServiceImpl;


@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/Authentication")
@Validated
public class AuthController {
    private final UserDetailsServiceImpl userDetailsService;
    private final AccessTokenServiceImpl accessTokenService;
    private final TokensServiceImpl tokensService;

    @PostMapping("/SignUp")
    public ResponseEntity<Void> register(
        @Valid @RequestBody RegisterUserRequest registerUserRequest
    ) {
        userDetailsService.registerUser(registerUserRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/SignIn")
    public ResponseEntity<TokensResponse> createToken(
        @Valid @RequestBody AccessTokenRequest accessTokenRequest
    ) {
        log.info("Получил запрос на аутентификацию пользователя: {}", accessTokenRequest.username());
        return ResponseEntity.ok(tokensService.createTokens(accessTokenRequest));
    }

    @PostMapping("/Refresh")
    public ResponseEntity<TokensResponse> refresh(
        @RequestBody RefreshTokenRequest request
    ) {
        log.info("Получил запрос на новую пару токенов: {}", request.refreshToken());
        return ResponseEntity.ok(tokensService.refreshTokens(request.refreshToken()));
    }

    @GetMapping("/Validate")
    public ResponseEntity<Claims> validate(
        @RequestParam("accessToken") @NotBlank String accessToken
    ) {
        log.info("Получил запрос на интроспекцию токена доступа : {}", accessToken);
        return ResponseEntity.ok(accessTokenService.extractAccessTokenPayload(accessToken));
    }
}
