package pl.muybien.asset;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.muybien.asset.dto.AssetAggregateDTO;
import pl.muybien.asset.dto.AssetGroupDTO;
import pl.muybien.asset.dto.AssetHistoryDTO;
import pl.muybien.customer.CustomerClient;
import pl.muybien.exception.AssetNotFoundException;
import pl.muybien.exception.OwnershipException;
import pl.muybien.finance.FinanceClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssetService {

    private final CustomerClient customerClient;
    private final AssetRepository repository;
    private final FinanceClient financeClient;

    @Transactional
    void createAsset(String authHeader, AssetRequest request) {
        var customerId = customerClient.fetchCustomerFromHeader(authHeader).id();
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
    void updateAsset(String authHeader, AssetRequest request, Long assetId) {
        var customerId = customerClient.fetchCustomerFromHeader(authHeader).id();
        var asset = repository.findById(assetId).orElseThrow(() ->
                new AssetNotFoundException("Asset with ID %s not found".formatted(assetId)));

        boolean customerIsNotOwner = !customerId.equals(asset.getCustomerId());
        if (customerIsNotOwner) {
            throw new OwnershipException("Asset updating failed:: Customer id mismatch");
        } else {
            asset.setCount(request.count().setScale(2, RoundingMode.HALF_UP));
            asset.setPurchasePrice(request.purchasePrice().setScale(2, RoundingMode.HALF_UP));
            asset.setCurrency(request.currency());

            repository.save(asset);
        }
    }

    @Transactional
    void deleteAsset(String authHeader, Long assetId) {
        var customerId = customerClient.fetchCustomerFromHeader(authHeader).id();
        var asset = repository.findById(assetId).orElseThrow(() ->
                new EntityNotFoundException("Asset with ID: %s not found".formatted(assetId)));

        boolean customerIsNotOwner = !customerId.equals(asset.getCustomerId());
        if (customerIsNotOwner) {
            throw new OwnershipException("Asset deletion failed:: Customer id mismatch");
        } else {
            repository.delete(asset);
        }
    }

    @Transactional(readOnly = true)
    List<AssetHistoryDTO> findAllAssetHistory(String authHeader) {
        var customerId = customerClient.fetchCustomerFromHeader(authHeader).id();

        List<AssetHistoryDTO> assetHistory = repository.findAssetHistoryByCustomerId(customerId);
        if (assetHistory.isEmpty()) {
            return Collections.emptyList();
        }

        return assetHistory.stream()
                .sorted((a1, a2) -> a2.createdDate().compareTo(a1.createdDate()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    List<AssetAggregateDTO> findAllCustomerAssets(String authHeader, String desiredCurrency) {
        var customerId = customerClient.fetchCustomerFromHeader(authHeader).id();
        var groupedAssets = repository
                .findAndAggregateAssetsByCustomerId(customerId)
                .orElse(Collections.emptyList());

        var aggregatedAssets = new ArrayList<AssetAggregateDTO>();
        groupedAssets.forEach(asset -> aggregatedAssets.add(aggregateAsset(asset, desiredCurrency)));

        return aggregatedAssets;
    }

    private AssetAggregateDTO aggregateAsset(AssetGroupDTO asset, String desiredCurrency) {
        String type = asset.assetType().name().toLowerCase();
        var finance = financeClient.findFinanceByTypeAndUri(type, asset.uri());

        BigDecimal currentPrice;
        BigDecimal value;
        BigDecimal averagePurchasePrice;
        BigDecimal totalInvested;
        BigDecimal totalValue;
        BigDecimal profit;
        BigDecimal profitPercentage;

        boolean assetCurrencyEqualsFinanceResponseCurrency = asset.currency().equalsIgnoreCase(finance.currency());
        if (assetCurrencyEqualsFinanceResponseCurrency) {
            currentPrice = finance.price();
        } else {
            var exchangeRate = financeClient.findExchangeRate(finance.currency(), asset.currency());
            currentPrice = finance.price().multiply(exchangeRate);
        }

        value = asset.count().multiply(currentPrice);
        averagePurchasePrice = BigDecimal.valueOf(asset.averagePurchasePrice());
        totalInvested = averagePurchasePrice.multiply(asset.count());
        totalValue = value;
        profit = totalValue.subtract(totalInvested);
        profitPercentage = totalInvested.compareTo(BigDecimal.ZERO) > 0
                ? profit.divide(totalInvested, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;

        BigDecimal exchangeRateToDesired = asset.currency().equalsIgnoreCase(desiredCurrency) ?
                BigDecimal.ONE :
                financeClient.findExchangeRate(asset.currency(), desiredCurrency);

        return AssetAggregateDTO.builder()
                .name(asset.name())
                .symbol(asset.symbol())
                .assetType(type)
                .count(asset.count())
                .currentPrice(currentPrice)
                .currency(asset.currency())
                .value(value)
                .averagePurchasePrice(averagePurchasePrice)
                .profit(profit)
                .profitInPercentage(profitPercentage)
                .exchangeRateToDesired(exchangeRateToDesired)
                .build();
    }
}