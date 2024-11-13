package ru.manannikov.testspringsecurityfilterjwt.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import ru.manannikov.testspringsecurityfilterjwt.model.UserEntity;

import java.util.Optional;


public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);
}
