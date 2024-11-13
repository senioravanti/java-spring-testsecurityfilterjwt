package ru.manannikov.testspringsecurityfilterjwt.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;


@Entity
@NoArgsConstructor
@Getter @Setter
@AllArgsConstructor
@Builder
@Table(name = "authorities")
public class AuthorityEntity implements GrantedAuthority {
    @Id
    @Column(name = "authority_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Short id;

    @Column(nullable = false, unique = true)
    private String authority;

    @Override
    public String getAuthority() {
        return authority;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof AuthorityEntity authorityEntity) {
            return this.authority.equals(authorityEntity.authority);
        }
        return false;
    }

    @Override
    public String toString() {return authority;}
}
