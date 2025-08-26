package pl.muybien.asset;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.lang.Nullable;
import pl.muybien.enums.AssetType;
import pl.muybien.enums.CurrencyType;
import pl.muybien.enums.UnitType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Builder
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "asset")
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    private String name;

    private String symbol;

    private String uri;

    private BigDecimal count;

    private BigDecimal purchasePrice;

    private String customerId;

    @Nullable
    private UnitType unitType;

    @Nullable
    private BigDecimal currentPrice;

    @Nullable
    private String comment;

    private CurrencyType currencyType;

    @Enumerated(EnumType.STRING)
    private AssetType assetType;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(insertable = false)
    private LocalDateTime lastModifiedDate;
}
