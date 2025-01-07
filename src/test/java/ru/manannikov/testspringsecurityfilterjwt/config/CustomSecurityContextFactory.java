package ru.manannikov.testspringsecurityfilterjwt.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.Collections;

@Log4j2
public class CustomSecurityContextFactory implements WithSecurityContextFactory<WithCustomUser> {
    @Override public SecurityContext createSecurityContext (WithCustomUser annotation) {
        logger.info("i'm running too");

        SecurityContext context = SecurityContextHolder.createEmptyContext();

        Authentication authentication = new UsernamePasswordAuthenticationToken(
            annotation.userId(), null, Collections.emptyList());
        context.setAuthentication(authentication);

        return context;
    }
}
