package pl.muybien.marketsync.asset;

import jakarta.persistence.*;
import lombok.*;
import pl.muybien.marketsync.wallet.Wallet;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "asset")
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private BigDecimal value;
    private BigDecimal count;
    private BigDecimal averagePurchasePrice;
    private Integer purchaseCount;
    private BigDecimal currentPrice;
    private Integer investmentPeriodInDays;
    private BigDecimal profitInPercentage;
    private BigDecimal profit;
    private LocalDateTime createdAt;

    @ManyToOne
    private Wallet wallet;
}
