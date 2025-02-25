package pl.muybien.asset;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.muybien.asset.dto.AssetAggregateDTO;
import pl.muybien.asset.dto.AssetGroupDTO;
import pl.muybien.asset.dto.AssetHistoryDTO;
import pl.muybien.exception.AssetNotFoundException;
import pl.muybien.exception.OwnershipException;
import pl.muybien.finance.FinanceClient;
import pl.muybien.finance.FinanceResponse;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssetService {

    private final AssetRepository repository;
    private final FinanceClient financeClient;

    @Transactional
    public void createAsset(String customerId, AssetRequest request) {
        var finance = financeClient.findFinanceByTypeAndUri(request.assetType(), request.uri());

        repository.save(Asset.builder()
                .assetType(AssetType.fromString(request.assetType()))
                .name(finance.name().substring(0, 1).toUpperCase() + finance.name().substring(1))
                .symbol(finance.symbol())
                .uri(request.uri())
                .count(request.count().setScale(2, RoundingMode.HALF_UP))
                .purchasePrice(request.purchasePrice().setScale(2, RoundingMode.HALF_UP))
                .currency(request.currency())
                .customerId(customerId)
                .build()
        );
    }

    @Transactional
    public void updateAsset(String customerId, AssetRequest request, Long assetId) {
        var asset = repository.findById(assetId).orElseThrow(() ->
                new AssetNotFoundException("Asset with ID %s not found".formatted(assetId)));

        boolean customerIsNotOwner = !customerId.equals(asset.getCustomerId());
        if (customerIsNotOwner) {
            throw new OwnershipException("Asset updating failed:: Customer id mismatch");
        }

        asset.setCount(request.count().setScale(2, RoundingMode.HALF_UP));
        asset.setPurchasePrice(request.purchasePrice().setScale(2, RoundingMode.HALF_UP));
        asset.setCurrency(request.currency());

        repository.save(asset);
    }

    @Transactional
    public void deleteAsset(String customerId, Long assetId) {
        var asset = repository.findById(assetId).orElseThrow(() ->
                new EntityNotFoundException("Asset with ID: %s not found".formatted(assetId)));

        boolean customerIsNotOwner = !customerId.equals(asset.getCustomerId());
        if (customerIsNotOwner) {
            throw new OwnershipException("Asset deletion failed:: Customer id mismatch");
        }

        repository.delete(asset);
    }

    @Transactional(readOnly = true)
    public List<AssetHistoryDTO> findAllAssetHistory(String customerId) {
        var assetHistory = repository.findAssetHistoryByCustomerId(customerId);

        return assetHistory.stream()
                .sorted(Comparator.comparing(AssetHistoryDTO::createdDate).reversed())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AssetAggregateDTO> findAllCustomerAssets(String customerId, String desiredCurrency) {
        var groupedAssets = repository.findAndAggregateAssetsByCustomerId(customerId).orElse(Collections.emptyList());
        var aggregatedAssets = new ArrayList<AssetAggregateDTO>();

        groupedAssets.forEach(asset -> aggregatedAssets.add(aggregateAsset(asset, desiredCurrency)));

        return aggregatedAssets;
    }

    private AssetAggregateDTO aggregateAsset(AssetGroupDTO asset, String desiredCurrency) {
        String type = asset.assetType().name().toLowerCase();
        var finance = financeClient.findFinanceByTypeAndUri(type, asset.uri());
        BigDecimal currentPrice = resolvePriceByCurrency(asset, finance);
        BigDecimal value = asset.count().multiply(currentPrice);
        BigDecimal totalInvested = asset.averagePurchasePrice().multiply(asset.count());
        BigDecimal profit = value.subtract(totalInvested);
        BigDecimal profitPercentage = resolveProfitInPercentage(totalInvested, profit);
        BigDecimal exchangeRateToDesired = resolveExchangeRateToDesired(asset.currency(), desiredCurrency);

        return new AssetAggregateDTO(
                asset.name(),
                asset.symbol(),
                type,
                asset.count(),
                resolvePriceByCurrency(asset, finance),
                asset.currency(),
                value,
                asset.averagePurchasePrice(),
                profit,
                profitPercentage,
                exchangeRateToDesired

        );
    }

    private BigDecimal resolvePriceByCurrency(AssetGroupDTO asset, FinanceResponse finance) {
        boolean currencyIsDifferent = !asset.currency().equalsIgnoreCase(finance.currency());
        if (currencyIsDifferent) {
            var exchangeRate = financeClient.findExchangeRate(finance.currency(), asset.currency());
            return finance.price().multiply(exchangeRate);
        }
        return finance.price();
    }

    private BigDecimal resolveProfitInPercentage(BigDecimal totalInvested, BigDecimal profit) {
        return totalInvested.compareTo(BigDecimal.ZERO) > 0
                ? profit.divide(totalInvested, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;
    }

    private BigDecimal resolveExchangeRateToDesired(String currency, String desiredCurrency) {
        return currency.equalsIgnoreCase(desiredCurrency) ?
                BigDecimal.ONE :
                financeClient.findExchangeRate(currency, desiredCurrency);
    }
}