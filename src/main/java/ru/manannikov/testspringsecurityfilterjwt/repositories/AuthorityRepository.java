package ru.manannikov.testspringsecurityfilterjwt.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import ru.manannikov.testspringsecurityfilterjwt.model.AuthorityEntity;


public interface AuthorityRepository extends JpaRepository<AuthorityEntity, Short> {
}
