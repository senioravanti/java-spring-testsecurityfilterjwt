package ru.manannikov.testspringsecurityfilterjwt.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SecureDigestAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import static ru.manannikov.testspringsecurityfilterjwt.utils.Constants.USER_ID_CLAIM;

@Log4j2
@Component
@RequiredArgsConstructor
public class AccessTokenUtils {
    @Value("${server.port}")
    private String serverPort;

    @Value("${app.security.access-token.lifetime}")
    private final Duration accessTokenLifetime;

    private final SecretKey hmacKey;

    public String createAccessToken(Map<String, Object> claims, String subject) {
        Instant issuedAt = Instant.now();

        SecureDigestAlgorithm<SecretKey, SecretKey> hmacAlgorithm = Jwts.SIG.HS256;
        return Jwts.builder()
            .subject(subject)
            .claims(claims)

            .issuer(String.format("http://localhost:%s", serverPort))
            .issuedAt(Date.from(issuedAt))
            .expiration(Date.from(issuedAt.plus(accessTokenLifetime)))

            .signWith(hmacKey, hmacAlgorithm)
            .compact();
    }

    public Jws<Claims> extractSignedClaims(String jwt) {
        return Jwts.parser()
            .verifyWith(hmacKey)
            .build()
                .parseSignedClaims(jwt);
    }

    public <T> T extractHeader(String jwt, Function<JwsHeader, T> claimsResolver) {
        Jws<Claims> jwsClaims = extractSignedClaims(jwt);
        return claimsResolver.apply(jwsClaims.getHeader());
    }

    /**
     * В качестве реализации функционального интерфейса Function передаем один из геттеров для стандартных пунктов полезной нагрузки токена, сделано чтобы не прописывать кучу лишних методов
     */
    private <T> T extractClaim(String jwt, Function<Claims, T> claimsResolver) {
        Jws<Claims> jwsClaims = extractSignedClaims(jwt);
        return claimsResolver.apply(jwsClaims.getPayload());
    }

    public String extractUsername(String jwt) {
        return extractClaim(jwt, Claims::getSubject);
    }

    private Instant extractExpiration(String jwt) {
        return extractClaim(jwt, Claims::getExpiration).toInstant();
    }

    public Object extractUserId(String jwt) {
        return extractClaim(
            jwt,
            (Claims claims) -> claims.get(USER_ID_CLAIM)
        );
    }

    /**
     * Пров., что тек. момент времени располагается на временной шкале перед моментом истечения срока действия токена
     */
    public boolean isAccessTokenExpired(String jwt) {
        Instant currentTime = Instant.now();
        Instant expirationTime = extractExpiration(jwt);
        logger.debug("Current time: {}; expiration time: {}", currentTime, expirationTime);
        return currentTime.isAfter(expirationTime);
    }
}
