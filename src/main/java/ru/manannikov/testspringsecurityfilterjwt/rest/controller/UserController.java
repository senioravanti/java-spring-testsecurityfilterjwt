package ru.manannikov.testspringsecurityfilterjwt.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.manannikov.testspringsecurityfilterjwt.rest.dto.UserDto;
import ru.manannikov.testspringsecurityfilterjwt.services.impl.UserDetailsServiceImpl;

@RestController
@RequestMapping("/Users")
@RequiredArgsConstructor
public class UserController {
    private final UserDetailsServiceImpl userDetailsService;

    @GetMapping("/Me")
    public ResponseEntity<UserDto> authenticatedUser() {
        Authentication authentication = SecurityContextHolder
            .getContext().getAuthentication();
        Long userId = (Long) authentication.getPrincipal();

        UserDto authenticatedUser = userDetailsService.loadUserById(userId);

        return ResponseEntity.ok(authenticatedUser);
    }
}
