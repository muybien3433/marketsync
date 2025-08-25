package pl.muybien.finance;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.muybien.currency.Currency;
import pl.muybien.currency.CurrencyRepository;
import pl.muybien.enums.AssetType;
import pl.muybien.enums.CurrencyType;
import pl.muybien.exception.FinanceNotFoundException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FinanceService {

    private static final int SCALE = 12;

    private final FinanceRepository financeRepository;
    private final CurrencyRepository currencyRepository; // TODO: Change to service in the future
    private final FinanceDetailDTOMapper mapper;

    @Transactional(readOnly = true)
    public FinanceResponse fetchFinance(AssetType assetType, String uri) {
        if (uri == null || uri.isBlank()) {
            throw new IllegalArgumentException("Finance identifier cannot be null or blank");
        }

        String normalizedUri = uri.toUpperCase();
        var financeDetail = resolveFinanceDetail(assetType, normalizedUri);

        return new FinanceResponse(
                financeDetail.name(),
                financeDetail.symbol(),
                financeDetail.uri(),
                financeDetail.unitType(),
                financeDetail.price(),
                financeDetail.currencyType(),
                financeDetail.assetType(),
                financeDetail.lastUpdated()
        );
    }

    private FinanceDetail resolveFinanceDetail(AssetType assetType, String normalizedUri) {
        var finance = financeRepository.findFinanceByAssetType(assetType)
                .orElseThrow(() ->
                        new FinanceNotFoundException("Finance not found for asset type: " + assetType.name())
                );

        var assetDetail = finance.getFinanceDetails().get(assetType);
        if (assetDetail == null) {
            throw new FinanceNotFoundException("No finance details found for asset type: " + assetType);
        }

        var financeDetail = assetDetail.get(normalizedUri);
        if (financeDetail == null) {
            throw new FinanceNotFoundException("Finance not found for uri: " + normalizedUri);
        }

        return financeDetail;
    }

    @Transactional(readOnly = true)
    public Set<FinanceDetailDTO> displayAvailableFinance(AssetType assetType) {
        var finance = financeRepository.findFinanceByAssetType(assetType)
                .orElseThrow(() -> new FinanceNotFoundException(
                        "Finance not found for asset type: " + assetType.name()));

        var financeDetails = finance.getFinanceDetails().get(assetType);
        if (financeDetails == null) {
            return Collections.emptySet();
        }

        return financeDetails.values().stream()
                .map(mapper::toDTO)
                .sorted(Comparator.comparing(FinanceDetailDTO::name))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Transactional(readOnly = true)
    public Set<FinanceDetailDTO> displayAvailableFinance(AssetType assetType, CurrencyType currencyType) {
        var finance = financeRepository.findFinanceByAssetType(assetType)
                .orElseThrow(() -> new FinanceNotFoundException(
                        "Finance not found for asset type: " + assetType.name()));

        Map<String, FinanceDetail> financeDetails = finance.getFinanceDetails().get(assetType);
        if (financeDetails == null) {
            return Collections.emptySet();
        }

        Map<CurrencyType, BigDecimal> cachedCurrencies = new ConcurrentHashMap<>();

        return financeDetails.values().stream()
                .map(mapper::toDTO)
                .sorted(Comparator.comparing(FinanceDetailDTO::name))
                .map(detail -> convertCurrencyIfNecessary(detail, currencyType, cachedCurrencies))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Transactional
    public FinanceDetailDTO convertCurrencyIfNecessary(
            FinanceDetailDTO detail, CurrencyType desiredCurrency, Map<CurrencyType, BigDecimal> cache) {

        BigDecimal originalPrice = new BigDecimal(detail.price());
        if (originalPrice.compareTo(BigDecimal.ZERO) == 0) {
            return new FinanceDetailDTO(
                    detail.name(),
                    detail.symbol(),
                    detail.uri(),
                    detail.unitType(),
                    "0.00",
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
                    updatedPrice.toPlainString(),
                    desiredCurrency,
                    detail.assetType(),
                    detail.lastUpdated()
            );
        }
        return detail;
    }

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
}
