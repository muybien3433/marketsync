package pl.muybien.finance;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.muybien.currency.CurrencyRepository;
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

    private final FinanceRepository financeRepository;
    private final CurrencyRepository currencyRepository;
    private final FinanceDetailDTOMapper mapper;

    @Transactional(readOnly = true)
    public FinanceResponse fetchFinance(String assetType, String uri) {
        String normalizedAssetType = assetType.toLowerCase();

        if (uri == null || uri.isBlank()) {
            throw new IllegalArgumentException("Finance identifier cannot be null or blank");
        }

        String normalizedUri = uri.toLowerCase();
        var financeDetail = resolveFinanceDetail(normalizedAssetType, normalizedUri);

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

    private FinanceDetail resolveFinanceDetail(String normalizedAssetType, String normalizedUri) {
        var finance = financeRepository.findFinanceByAssetType(normalizedAssetType)
                .orElseThrow(() ->
                        new FinanceNotFoundException("Finance not found for asset type: " + normalizedAssetType)
                );

        var assetDetail = finance.getFinanceDetails().get(normalizedAssetType);
        if (assetDetail == null) {
            throw new FinanceNotFoundException("No finance details found for asset type: " + normalizedAssetType);
        }

        var financeDetail = assetDetail.get(normalizedUri);
        if (financeDetail == null) {
            throw new FinanceNotFoundException("Finance not found for uri: " + normalizedUri);
        }

        return financeDetail;
    }

    @Transactional(readOnly = true)
    public Set<FinanceDetailDTO> displayAvailableFinance(String assetType) {
        String normalizedAssetType = assetType.toLowerCase();
        var finance = financeRepository.findFinanceByAssetType(normalizedAssetType)
                .orElseThrow(() -> new FinanceNotFoundException(
                        "Finance not found for asset type: " + normalizedAssetType));

        var financeDetails = finance.getFinanceDetails().get(normalizedAssetType);

        if (financeDetails == null) {
            return Collections.emptySet();
        }

        return financeDetails.values().stream()
                .map(mapper::toDTO)
                .sorted(Comparator.comparing(FinanceDetailDTO::name))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Transactional(readOnly = true)
    public Set<FinanceDetailDTO> displayAvailableFinance(String assetType, String currencyType) {
        String normalizedAssetType = assetType.toLowerCase();
        var finance = financeRepository.findFinanceByAssetType(normalizedAssetType)
                .orElseThrow(() -> new FinanceNotFoundException(
                        "Finance not found for asset type: " + normalizedAssetType));

        var financeDetails = finance.getFinanceDetails().get(normalizedAssetType);
        if (financeDetails == null) {
            return Collections.emptySet();
        }

        CurrencyType resolvedDesiredCurrency = CurrencyType.valueOf(currencyType.toUpperCase());
        Map<CurrencyType, BigDecimal> cachedCurrencies = new ConcurrentHashMap<>();

        return financeDetails.values().stream()
                .map(mapper::toDTO)
                .sorted(Comparator.comparing(FinanceDetailDTO::name))
                .map(detail -> convertCurrencyIfNecessary(detail, resolvedDesiredCurrency, cachedCurrencies))
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
                    desiredCurrency.name(),
                    detail.assetType(),
                    detail.lastUpdated()
            );
        }

        CurrencyType sourceCurrency = CurrencyType.valueOf(detail.currencyType().toUpperCase());
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
                    desiredCurrency.name(),
                    detail.assetType(),
                    detail.lastUpdated()
            );
        }
        return detail;
    }

    @Transactional(readOnly = true)
    public BigDecimal findExchangeRate(CurrencyType from, CurrencyType to) {
        var exchange = currencyRepository.findCurrencyByName(currencyNameResolver(from, to))
                .orElseThrow(() -> new FinanceNotFoundException(
                        "Could not find currency pair for " + from + " to " + to));

        return exchange.getExchange();
    }

    String currencyNameResolver(CurrencyType from, CurrencyType to) {
        return from.name().toLowerCase() + "-" + to.name().toLowerCase();
    }
}
