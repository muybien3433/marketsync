package pl.muybien.finance.finance;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record Finance(
        String name,
        BigDecimal priceUsd
) {
}
