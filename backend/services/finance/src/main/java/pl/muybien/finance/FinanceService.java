package pl.muybien.finance;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.muybien.exception.FinanceNotFoundException;
import pl.muybien.finance.currency.CurrencyService;

import java.math.BigDecimal;
import java.util.*;
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
                financeDetail.price(),
                financeDetail.currency(),
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
}
