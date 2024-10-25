package pl.muybien.marketsync.asset.crypto;

import jakarta.persistence.Table;
import lombok.*;
import pl.muybien.marketsync.asset.Asset;

import java.math.BigDecimal;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "crypto")
public class Crypto implements Asset {

    private String symbol;
    private String name;
    private BigDecimal priceUsd;
}
