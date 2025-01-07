package ru.manannikov.testspringsecurityfilterjwt.services.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ru.manannikov.testspringsecurityfilterjwt.utils.AccessTokenUtils;

import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class AccessTokenServiceImpl {

    private final AccessTokenUtils accessTokenUtils;

    public String extractUsername(String jwt)
        throws UnsupportedJwtException
    {
        String username = accessTokenUtils.extractUsername(jwt);
        logger.debug("extracted username: {}", username);

        if (username == null || username.isBlank()) {
            throw new UnsupportedJwtException("There is no mandatory claim \"sub\" in Jwt");
        }

        return username;
    }

    public Long extractUserId(String jwt) {
        Long userId = Optional
            .ofNullable(accessTokenUtils.extractUserId(jwt))
            .map(
                id -> Long.valueOf(id.toString())
            )
            .orElseThrow(
                () -> new UnsupportedJwtException("В полезной нагрузке Jwt отсутствует пункт user_id")
            )
            ;
        logger.debug("userId extracted from jwt body: {}", userId);

        return userId;
    }

    public Claims extractAccessTokenPayload(String jwt) {
        return accessTokenUtils.extractSignedClaims(jwt).getPayload();
    }
}
