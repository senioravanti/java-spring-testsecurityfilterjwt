package ru.manannikov.testspringsecurityfilterjwt.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;


@Component
@NoArgsConstructor
@Getter @Setter
@ConfigurationProperties("app.security.access-token.signature")
public class SignatureProperties {
    private String algorithm;
    private String secret;
}
