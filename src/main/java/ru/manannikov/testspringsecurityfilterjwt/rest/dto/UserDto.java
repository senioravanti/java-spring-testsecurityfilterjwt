package ru.manannikov.testspringsecurityfilterjwt.rest.dto;

import ru.manannikov.testspringsecurityfilterjwt.model.AuthorityEntity;
import ru.manannikov.testspringsecurityfilterjwt.model.UserEntity;

import java.util.List;

public record UserDto(
    Long id,
    String username,
    List<String> authorities
) {
    public static UserDto toDto(UserEntity userEntity) {
        return new UserDto(
            userEntity.getId(),
            userEntity.getUsername(),
            userEntity.getAuthorities().stream().map(AuthorityEntity::getAuthority).toList()
        );
    }
}
