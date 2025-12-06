package pl.muybien.finance;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.muybien.entity.Currency;
import pl.muybien.enumeration.CurrencyType;
import pl.muybien.finance.dto.FinanceDetailDTO;
import pl.muybien.repository.CurrencyRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Objects;

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
                .orElseThrow(() -> new IllegalStateException("Missing rate USD/" + currencyType.name()));

        if (rate.signum() <= 0) {
            throw new IllegalStateException("Non-positive rate for USD/" + currencyType.name() + ": " + rate);
        }

        return rate.setScale(SCALE, RoundingMode.HALF_UP);
    }

    @Transactional(readOnly = true)
    public FinanceDetailDTO convertCurrencyIfNecessary(
            FinanceDetailDTO detail, CurrencyType desiredCurrency, Map<CurrencyType, BigDecimal> cache) {
        BigDecimal originalPrice = detail.price();
        if (originalPrice.compareTo(BigDecimal.ZERO) == 0) {
            return new FinanceDetailDTO(
                    detail.name(),
                    detail.symbol(),
                    detail.uri(),
                    detail.unitType(),
                    BigDecimal.ZERO,
                    desiredCurrency,
                    detail.assetType(),
                    detail.lastUpdated()
            );
        }

        CurrencyType sourceCurrency = detail.currencyType();
        boolean isExchangeNeeded = !Objects.equals(sourceCurrency, desiredCurrency);

        if (isExchangeNeeded) {
            BigDecimal rate = cache.computeIfAbsent(
                    sourceCurrency,
                    key -> findExchangeRate(key, desiredCurrency)
            );

            BigDecimal updatedPrice = originalPrice.multiply(rate);
            return new FinanceDetailDTO(
                    detail.name(),
                    detail.symbol(),
                    detail.uri(),
                    detail.unitType(),
                    updatedPrice,
                    desiredCurrency,
                    detail.assetType(),
                    detail.lastUpdated()
            );
        }
        return detail;
    }
}
