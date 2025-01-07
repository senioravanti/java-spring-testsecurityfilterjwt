package ru.manannikov.testspringsecurityfilterjwt.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import ru.manannikov.testspringsecurityfilterjwt.model.AuthorityEntity;

import java.util.Optional;


public interface AuthorityRepository extends JpaRepository<AuthorityEntity, Short> {
    Optional<AuthorityEntity> findByAuthority(String authority);
}
