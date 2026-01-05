package pl.muybien.entity.wallet;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.lang.Nullable;
import pl.muybien.entity.AbstractAuditingEntity;
import pl.muybien.enumeration.AssetType;
import pl.muybien.enumeration.CurrencyType;
import pl.muybien.enumeration.UnitType;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Builder
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "asset")
public class Asset extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    private String name;

    private String symbol;

    private String uri;

    @Column(precision = 38, scale = 18)
    private BigDecimal count;

    @Column(precision = 38, scale = 18)
    private BigDecimal purchasePrice;

    private UUID customerId;

    @Nullable
    @Enumerated(EnumType.STRING)
    private UnitType unitType;

    @Nullable
    @Column(precision = 38, scale = 18)
    private BigDecimal currentPrice;

    @Nullable
    private String comment;

    @Enumerated(EnumType.STRING)
    private CurrencyType currencyType;

    @Enumerated(EnumType.STRING)
    private AssetType assetType;
}
