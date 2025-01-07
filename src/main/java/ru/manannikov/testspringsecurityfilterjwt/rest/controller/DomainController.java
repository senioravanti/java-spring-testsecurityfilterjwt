package ru.manannikov.testspringsecurityfilterjwt.rest.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Log4j2
@RestController
@RequestMapping("/Test")
public class DomainController {
    // VVV
    // curl --get -H 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzZW5pb3JhdmFudGkiLCJ1c2VyX2lkIjoxLCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgwMDEiLCJpYXQiOjE3MzI3MTQyMTcsImV4cCI6MTczMjcxNTExN30.QvqOljTlgD7ysvd_qNWZHUcRNIDV76nJMCeV6uLYmC8' 'http://localhost:8001/Test'
    @GetMapping({"", "/"})
    public ResponseEntity<String> getPrincipal() {
        logger.info("running getPrincipal");
        Long userId = (Long) Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).orElseThrow(() -> new RuntimeException("userId is null"));

        logger.debug("return userId: {}", userId);

        return ResponseEntity.ok(String.format("you got userId: %d :)", userId));
    }
}
