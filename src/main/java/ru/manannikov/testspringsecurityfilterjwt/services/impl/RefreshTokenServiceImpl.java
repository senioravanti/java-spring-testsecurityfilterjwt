package ru.manannikov.testspringsecurityfilterjwt.services.impl;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.manannikov.testspringsecurityfilterjwt.exceptions.ResourceNotFoundException;
import ru.manannikov.testspringsecurityfilterjwt.model.RefreshTokenEntity;
import ru.manannikov.testspringsecurityfilterjwt.model.UserEntity;
import ru.manannikov.testspringsecurityfilterjwt.repositories.RefreshTokenRepository;
import ru.manannikov.testspringsecurityfilterjwt.repositories.UserRepository;
import ru.manannikov.testspringsecurityfilterjwt.rest.dto.UserDto;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl {
    @Value("${app.security.refresh-token.lifetime}")
    private Duration refreshTokenLifetime;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    private RefreshTokenEntity getByRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByRefreshToken(refreshToken).orElseThrow(
            () -> new ResourceNotFoundException(
                String.format("Refresh token %s not found", refreshToken)
            )
        );
    }

    public UserDto loadByRefreshToken(String refreshToken) {
        RefreshTokenEntity refreshTokenEntity = getByRefreshToken(refreshToken);

        return UserDto.toDto(refreshTokenEntity.getUser());
    }

    public String createRefreshToken(String username) {
        UserEntity userEntity = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(String.format("Пользователь %s не зарегистрирован", username)));

        final Instant expirationDate = Instant.now().plus(refreshTokenLifetime);
        final String refreshToken = UUID.randomUUID().toString();

        RefreshTokenEntity refreshTokenEntity = Optional.ofNullable(userEntity.getRefreshToken())
            .map(
                existingRefreshTokenEntity -> {

                    existingRefreshTokenEntity.setRefreshToken(refreshToken);
                    existingRefreshTokenEntity.setExpirationDate(expirationDate);

                    return existingRefreshTokenEntity;
                }
            )

            .orElse(
                RefreshTokenEntity.builder()
                    .refreshToken(refreshToken)
                    .expirationDate(expirationDate)
                    .user(userEntity)
                .build()
            )
        ;

        return refreshTokenRepository.save(refreshTokenEntity).getRefreshToken();
    }

    public boolean isRefreshTokenExpired(String refreshToken) {
        RefreshTokenEntity refreshTokenEntity = getByRefreshToken(refreshToken);

        return Instant.now().isAfter(refreshTokenEntity.getExpirationDate());
    }

    @Transactional
    public void deleteExpiredRefreshToken(String refreshToken) {
        if (!isRefreshTokenExpired(refreshToken)) {
            throw new RuntimeException(String.format("Refresh token %s is not expired", refreshToken));
        }

        refreshTokenRepository.deleteByRefreshToken(refreshToken);
    }
}
