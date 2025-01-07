package ru.manannikov.testspringsecurityfilterjwt.rest.controller;

import io.jsonwebtoken.security.Request;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import ru.manannikov.testspringsecurityfilterjwt.rest.dto.AccessTokenRequest;
import ru.manannikov.testspringsecurityfilterjwt.rest.dto.TokensResponse;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.web.util.UriComponentsBuilder.fromHttpUrl;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Log4j2
public class DomainControllerIT {
    // Это значение задается на этапе выполнения, а не в application.properties
    @LocalServerPort
    private int port = 0;

    @Autowired
    private TestRestTemplate restTemplate = null;

    @Test
    public void testGetPrincipal()
        throws URISyntaxException
    {
        AccessTokenRequest accessTokenRequest = new AccessTokenRequest("senioravanti", "12345");
        HttpEntity<AccessTokenRequest> accessTokenRequestBody = new HttpEntity<>(accessTokenRequest);

        TokensResponse response = restTemplate.postForObject(String.format("http://localhost:%d/Authentication/SignIn", port), accessTokenRequestBody, TokensResponse.class);

        assertAll(
            () -> assertNotNull(response),
            () -> assertNotNull(response.accessToken())
        );
        logger.debug("access token response: {}", response);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(response.accessToken());

//        RequestEntity<HttpHeaders> principalRequest = new RequestEntity<>(
//            headers,
//            HttpMethod.GET,
//            new URI(String.format("http://localhost:%d/Test", port))
//        );
        HttpEntity<Void> principalRequest = new HttpEntity<>(headers);
        UriComponents uriComponents = fromHttpUrl(String.format("http://localhost:%d/Test", port)).build();

//        ResponseEntity<String> principalResponse = restTemplate.exchange(principalRequest, String.class);
        ResponseEntity<String> principalResponse = restTemplate.exchange(uriComponents.toUriString(), HttpMethod.GET, principalRequest, String.class);

        assertNotNull(principalResponse);
        logger.debug("principal response: {}", principalResponse);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("last_records", String.valueOf(1));

        uriComponents = UriComponentsBuilder.fromHttpUrl(String.format("http://localhost:%d/Test", port)).queryParams(params).build();
        logger.debug("result uri: {}", uriComponents.toUriString());
    }
}
