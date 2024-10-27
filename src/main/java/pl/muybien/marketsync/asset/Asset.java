package pl.muybien.marketsync.asset;

import jakarta.persistence.*;
import pl.muybien.marketsync.wallet.Wallet;

import java.math.BigDecimal;

@Entity
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

    @ManyToOne
    private Wallet wallet;
}
