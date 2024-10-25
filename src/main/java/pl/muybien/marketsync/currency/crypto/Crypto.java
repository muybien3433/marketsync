package pl.muybien.marketsync.currency.crypto;

import jakarta.persistence.Table;
import lombok.*;
import pl.muybien.marketsync.currency.Currency;

import java.math.BigDecimal;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "crypto")
public class Crypto implements Currency {

    private String symbol;
    private String name;
    private BigDecimal priceUsd;
}
