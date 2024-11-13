package pl.muybien.walletservice.asset;

import jakarta.persistence.*;
import lombok.*;
import pl.muybien.marketsync.wallet.Wallet;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Builder
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "asset")
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private BigDecimal value;
    private BigDecimal count;
    private BigDecimal averagePurchasePrice;
    private LocalDate investmentStartDate;

    @ManyToOne
    private Wallet wallet;
}
