package pl.muybien.finance;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record Finance(
        String name,
        BigDecimal price,
        String currency
) {
}
