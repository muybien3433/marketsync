package pl.muybien.finance;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.muybien.entity.Currency;
import pl.muybien.enumeration.CurrencyType;
import pl.muybien.exception.FinanceNotFoundException;
import pl.muybien.repository.CurrencyRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
@Slf4j
public class CurrencyService {
    private final CurrencyRepository currencyRepository;

    private static final int SCALE = 12;

    @Transactional(readOnly = true)
    public BigDecimal findExchangeRate(CurrencyType from, CurrencyType to) {
        if (from == null || to == null) throw new IllegalArgumentException("Currencies required");
        if (from == to) return BigDecimal.ONE;

        // Short-circuits for USD to avoid an extra divide
        if (from == CurrencyType.USD) return usdTo(to);                         // USD -> B = USD->B
        if (to   == CurrencyType.USD) return BigDecimal.ONE.divide(usdTo(from), SCALE, RoundingMode.HALF_UP); // A -> USD

        // General cross: A/B = (USD->B) / (USD->A)
        BigDecimal usdToFrom = usdTo(from);
        BigDecimal usdToTo   = usdTo(to);
        return usdToTo.divide(usdToFrom, SCALE, RoundingMode.HALF_UP);
    }

    private BigDecimal usdTo(CurrencyType currencyType) {
        if (currencyType == CurrencyType.USD) return BigDecimal.ONE;

        BigDecimal rate = currencyRepository
                .findById(currencyType.name()) // _id is the code (e.g., "PLN")
                .map(Currency::getExchangeFromUSD) // 1 USD = X <code>
                .orElseThrow(() -> new FinanceNotFoundException("Missing rate USD/" + currencyType.name()));

        if (rate.signum() <= 0) {
            throw new IllegalStateException("Non-positive rate for USD/" + currencyType.name() + ": " + rate);
        }

        return rate.setScale(SCALE, RoundingMode.HALF_UP);
    }
}
