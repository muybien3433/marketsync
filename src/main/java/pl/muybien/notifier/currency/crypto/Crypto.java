package pl.muybien.notifier.currency.crypto;

import lombok.*;

import java.math.BigDecimal;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Crypto {

    private String id;
    private String rank;
    private String symbol;
    private String name;
    private String supply;
    private String maxSupply;
    private String marketCapUsd;
    private String volumeUsd24Hr;
    private BigDecimal priceUsd;
    private String changePercent24Hr;
    private String vwap24Hr;
    private String explorer;
}
