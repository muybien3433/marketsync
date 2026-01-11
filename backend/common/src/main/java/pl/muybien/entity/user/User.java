package pl.muybien.entity.user;

import jakarta.persistence.*;
import lombok.*;
import pl.muybien.entity.AbstractAuditingEntity;

import java.util.UUID;

@Entity
@Builder
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "app_user")
public class User extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @EqualsAndHashCode.Include
    private UUID keycloakId;

    @OneToOne(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER,
            orphanRemoval = true
    )
    private UserConfig userConfig;
}
