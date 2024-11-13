package ru.manannikov.testspringsecurityfilterjwt.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import ru.manannikov.testspringsecurityfilterjwt.model.RefreshTokenEntity;

import java.util.Optional;


public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Integer> {
    Optional<RefreshTokenEntity> findByRefreshToken(String token);
    void deleteByRefreshToken(String token);
}
