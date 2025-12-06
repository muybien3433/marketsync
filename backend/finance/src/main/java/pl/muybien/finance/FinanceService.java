package pl.muybien.finance;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.muybien.entity.helper.FinanceDetail;
import pl.muybien.finance.dto.FinanceBaseDTO;
import pl.muybien.finance.dto.FinanceDetailDTO;
import pl.muybien.repository.FinanceRepository;
import pl.muybien.response.FinanceResponse;
import pl.muybien.enumeration.AssetType;
import pl.muybien.enumeration.CurrencyType;
import pl.muybien.exception.FinanceNotFoundException;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FinanceService {
    private final FinanceRepository financeRepository;
    private final CurrencyService currencyService;
    private final FinanceDTOMapper mapper;

    @Transactional(readOnly = true)
    public FinanceResponse fetchFinance(AssetType assetType, String uri) {
        if (uri == null || uri.isBlank()) {
            throw new IllegalArgumentException("Finance identifier cannot be null or blank");
        }

        String normalizedUri = uri.toLowerCase();
        var financeDetail = resolveFinanceDetail(assetType, normalizedUri);

        return mapper.toResponse(financeDetail);
    }

    @Transactional(readOnly = true)
    public FinanceResponse fetchFinance(AssetType assetType, String uri, CurrencyType currencyType) {
        if (uri == null || uri.isBlank()) {
            throw new IllegalArgumentException("Finance identifier cannot be null or blank");
        }

        String normalizedUri = uri.toLowerCase();
        var financeDetail = resolveFinanceDetail(assetType, normalizedUri);
        BigDecimal exchangeRate = currencyService.findExchangeRate(financeDetail.currencyType(), currencyType);
        BigDecimal price = exchangeRate.compareTo(BigDecimal.ONE) == 0
                ? financeDetail.price()
                : financeDetail.price().multiply(exchangeRate);

        return mapper.toResponseWithPriceAndCurrencyType(financeDetail, price, currencyType);
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
                .map(mapper::toDetailDTO)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Transactional(readOnly = true)
    public Set<FinanceBaseDTO> displayAvailableFinanceBase(AssetType assetType) {
        var finance = financeRepository.findFinanceByAssetType(assetType)
                .orElseThrow(() -> new FinanceNotFoundException(
                        "Finance not found for asset type: " + assetType.name()));

        var financeDetails = finance.getFinanceDetails().get(assetType);
        if (financeDetails == null) {
            return Collections.emptySet();
        }

        return financeDetails.values().stream()
                .sorted(Comparator.comparing(FinanceDetail::position, Comparator.nullsLast(Integer::compareTo)))
                .map(mapper::toBaseDTO)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
