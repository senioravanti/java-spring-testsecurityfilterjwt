package ru.manannikov.testspringsecurityfilterjwt.services.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import ru.manannikov.testspringsecurityfilterjwt.model.SecurityUser;
import ru.manannikov.testspringsecurityfilterjwt.rest.dto.AccessTokenRequest;
import ru.manannikov.testspringsecurityfilterjwt.rest.dto.TokensResponse;
import ru.manannikov.testspringsecurityfilterjwt.rest.dto.UserDto;
import ru.manannikov.testspringsecurityfilterjwt.utils.AccessTokenUtils;

import java.time.Duration;
import java.util.Map;

import static ru.manannikov.testspringsecurityfilterjwt.utils.Constants.USER_ID_CLAIM;


@Log4j2
@Service
@RequiredArgsConstructor
public class TokensServiceImpl {
    @Value("${app.security.access-token.lifetime}")
    private final Duration accessTokenLifetime;

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    private final AccessTokenUtils accessTokenUtils;
    private final RefreshTokenServiceImpl refreshTokenService;


    private TokensResponse createTokenResponse (Long userId, String username) {

        final String accessToken = accessTokenUtils.createAccessToken(Map.of(USER_ID_CLAIM, userId), username);
        final String refreshToken = refreshTokenService.createRefreshToken(username);

        return new TokensResponse(accessToken, refreshToken, accessTokenLifetime.getSeconds());
    }

    public TokensResponse createTokens(AccessTokenRequest accessTokenRequest) {

        logger.info("Приступаю к обработке запроса на аутентификацию пользователя {}", accessTokenRequest.username());
        
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
            accessTokenRequest.username(),
            accessTokenRequest.password()
        ));

        final SecurityUser securityUser = (SecurityUser) userDetailsService.loadUserByUsername(accessTokenRequest.username());

        return createTokenResponse(securityUser.getId(), securityUser.getUsername());
    }

    public TokensResponse refreshTokens(String refreshToken) {
        if (refreshTokenService.isRefreshTokenExpired(refreshToken)) {
            refreshTokenService.deleteExpiredRefreshToken(refreshToken);
            throw new RuntimeException("Refresh token is expired");
        }

        UserDto subject = refreshTokenService.loadByRefreshToken(refreshToken);

        return createTokenResponse(subject.id(), subject.username());
    }
}
