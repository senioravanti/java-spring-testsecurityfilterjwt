
package ru.manannikov.testspringsecurityfilterjwt.config;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import ru.manannikov.testspringsecurityfilterjwt.services.impl.AccessTokenServiceImpl;
import ru.manannikov.testspringsecurityfilterjwt.services.impl.TokensServiceImpl;
import ru.manannikov.testspringsecurityfilterjwt.utils.AccessTokenUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

@Log4j2
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final HandlerExceptionResolver handlerExceptionResolver;
    private final AccessTokenUtils accessTokenUtils;
    private final AccessTokenServiceImpl accessTokenService;

    private String extractJwt(String authorizationHeader) {
        String[] headerParts = authorizationHeader.split("\\s+");
        if (headerParts.length != 2 || !headerParts[0].equals("Bearer")) {
            throw new BadCredentialsException("Invalid authorization header format with a bearer token authorization scheme: " + authorizationHeader);
        }
        return headerParts[1];
    }

    @Override
    protected void doFilterInternal (
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    )
        throws ServletException, IOException
    {
        final String authorizationHeader = request.getHeader("Authorization");
        log.debug("authorizationHeader: {}", authorizationHeader);

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        var authenticationOptional = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication());
        log.debug("Security context before authentication: {}", authenticationOptional.orElse(null));

        try {
            if (authenticationOptional.isPresent()) {
                throw new RuntimeException(String.format("User %s is already authenticated", authenticationOptional.get().getName()));
            }

            String jwt = extractJwt(authorizationHeader);
            log.debug("JWT token: {}", jwt);

            String username = accessTokenService.extractUsername(jwt);

            if (accessTokenUtils.isAccessTokenExpired(jwt)) {
                var claims = accessTokenUtils.extractSignedClaims(jwt);
                throw new ExpiredJwtException(claims.getHeader(), claims.getPayload(), "The JWT token has expired");
            }

            Long userId = accessTokenService.extractUserId(jwt);

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userId, null,
                Collections.emptyList()
            );
            authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.debug("Security context after authentication: {}",
                SecurityContextHolder.getContext().getAuthentication().getPrincipal()
            );
        }
        catch (BadCredentialsException | ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException ex) {
            log.error("JWT validation exception");

            handlerExceptionResolver.resolveException(request, response, null, ex);
        }

        filterChain.doFilter(request, response);
    }
}
