package pl.muybien.subscriptionservice.finance.crypto;

import jakarta.persistence.Table;
import pl.muybien.marketsync.finance.Finance;

import java.math.BigDecimal;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "crypto")
public class Crypto implements Finance {

    private String symbol;
    private String name;
    private BigDecimal priceUsd;
}