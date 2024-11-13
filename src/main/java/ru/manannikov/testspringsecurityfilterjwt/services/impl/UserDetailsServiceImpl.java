package ru.manannikov.testspringsecurityfilterjwt.services.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.manannikov.testspringsecurityfilterjwt.exceptions.ResourceNotFoundException;
import ru.manannikov.testspringsecurityfilterjwt.model.AuthorityEntity;
import ru.manannikov.testspringsecurityfilterjwt.model.SecurityUser;
import ru.manannikov.testspringsecurityfilterjwt.model.UserEntity;
import ru.manannikov.testspringsecurityfilterjwt.repositories.UserRepository;
import ru.manannikov.testspringsecurityfilterjwt.rest.dto.RegisterUserRequest;
import ru.manannikov.testspringsecurityfilterjwt.rest.dto.UserDto;

import java.util.List;


@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;
    private final AuthorityServiceImpl authorityService;

    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).map(SecurityUser::new).orElseThrow(() -> new UsernameNotFoundException(String.format("Пользователь %s не зарегистрирован", username)));
    }

    public UserDto loadUserById(Long userId) {
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException(String.format("Пользователь с идентификатором %d не зарегистрирован", userId)));

        return UserDto.toDto(userEntity);
    }

    public void registerUser(RegisterUserRequest request) {
        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new RuntimeException(String.format("User %s already registered", request.username()));
        }

        List<AuthorityEntity> authorities = request.authorities().stream()
            .map(authorityService::findByAuthority).toList()
            ;

        UserEntity userEntity = UserEntity.builder()
            .username(request.username())
            .password(passwordEncoder.encode(request.password()))
            .authorities(authorities)
            .build();

        userRepository.save(userEntity);
    }
}
