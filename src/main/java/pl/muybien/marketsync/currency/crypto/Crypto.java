package pl.muybien.marketsync.currency.crypto;

import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "crypto")
public class Crypto {

    private String symbol;
    private String name;
    private BigDecimal priceUsd;
}
