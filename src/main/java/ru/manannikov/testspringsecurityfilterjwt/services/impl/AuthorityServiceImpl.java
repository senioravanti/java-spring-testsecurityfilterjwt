package ru.manannikov.testspringsecurityfilterjwt.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.manannikov.testspringsecurityfilterjwt.exceptions.ResourceNotFoundException;
import ru.manannikov.testspringsecurityfilterjwt.model.AuthorityEntity;
import ru.manannikov.testspringsecurityfilterjwt.repositories.AuthorityRepository;

@Service
@RequiredArgsConstructor
public class AuthorityServiceImpl {
    private final AuthorityRepository authorityRepository;

    public AuthorityEntity findByAuthority(String authority) {
        return authorityRepository.findByAuthority(authority).orElseThrow(() -> new ResourceNotFoundException(String.format("Authority %s does not exist", authority)));
    }
}
