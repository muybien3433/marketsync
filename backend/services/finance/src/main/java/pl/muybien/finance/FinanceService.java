package pl.muybien.finance;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.muybien.exception.FinanceNotFoundException;
import pl.muybien.finance.currency.CurrencyService;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FinanceService {

    private final CurrencyService currencyService;
    private final FinanceRepository repository;
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
                financeDetail.price().toPlainString(),
                financeDetail.currencyDetail(),
                financeDetail.assetType(),
                financeDetail.lastUpdated()
        );
    }

    private FinanceDetail resolveFinanceDetail(String normalizedAssetType, String normalizedUri) {
        var finance = repository.findFinanceByAssetType(normalizedAssetType)
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
    public BigDecimal findExchangeRate(CurrencyType from, CurrencyType to) {
        return currencyService.getCurrencyPairExchange(from, to);
    }

    @Transactional(readOnly = true)
    public Set<FinanceDetailDTO> displayAvailableFinance(String assetType) {
        String normalizedAssetType = assetType.toLowerCase();
        var finance = repository.findFinanceByAssetType(normalizedAssetType)
                .orElseThrow(() -> new FinanceNotFoundException("Finance not found for asset type: " + normalizedAssetType));

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
    public Set<FinanceDetailDTO> displayAvailableFinanceByCurrency(String assetType, String currencyType) {
        String normalizedAssetType = assetType.toLowerCase();
        var finance = repository.findFinanceByAssetType(normalizedAssetType)
                .orElseThrow(() -> new FinanceNotFoundException("Finance not found for asset type: " + normalizedAssetType));

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

    private FinanceDetailDTO convertCurrencyIfNecessary(
            FinanceDetailDTO detail, CurrencyType desiredCurrency, Map<CurrencyType, BigDecimal> cache) {

        CurrencyType sourceCurrency = CurrencyType.valueOf(detail.currencyType().toUpperCase());
        if (!sourceCurrency.equals(desiredCurrency)) {
            BigDecimal rate = cache.computeIfAbsent(
                    sourceCurrency,
                    key -> currencyService.getCurrencyPairExchange(key, desiredCurrency)
            );

            BigDecimal updatedPrice = new BigDecimal(detail.price()).multiply(rate);
            return new FinanceDetailDTO(
                    detail.name(),
                    detail.symbol(),
                    detail.uri(),
                    updatedPrice.toPlainString(),
                    desiredCurrency.name(),
                    detail.assetType(),
                    detail.lastUpdated()
            );
        }
        return detail;
    }
}
