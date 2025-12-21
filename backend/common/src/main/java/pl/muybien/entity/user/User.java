package pl.muybien.entity.user;

import jakarta.persistence.*;
import lombok.*;
import pl.muybien.entity.AbstractAuditingEntity;
import pl.muybien.enumeration.CurrencyType;
import pl.muybien.enumeration.LanguageType;

import java.util.UUID;

@Entity
@Builder
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user")
public class User extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @EqualsAndHashCode.Include
    private String keycloakId;

    @Enumerated(EnumType.STRING)
    private LanguageType language;

    @Enumerated(EnumType.STRING)
    private CurrencyType currency;
}
