package pl.muybien.asset;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
        repository.save(Asset.builder()
                .type(AssetType.fromString(request.type()))
                .name(request.uri())
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
    List<AssetDTO> findAllCustomerAssets(String authHeader, String desiredCurrency) {
        var customerId = customerClient.fetchCustomerFromHeader(authHeader).id();

        var groupedAssets = repository
                .findAndAggregateAssetsByCustomerId(customerId)
                .orElse(Collections.emptyList());

        var aggregatedAssets = new ArrayList<AssetDTO>();
        groupedAssets.forEach(asset -> aggregatedAssets.add(aggregateAsset(asset, desiredCurrency)));

        return aggregatedAssets;
    }

    private AssetDTO aggregateAsset(AssetGroupDTO asset, String desiredCurrency) {
        String type = asset.type().name().toLowerCase();
        var finance = financeClient.findFinanceByUriAndTypeAndCurrency(type, asset.uri(), desiredCurrency);

        BigDecimal value = asset.count().multiply(finance.price()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal averagePurchasePrice = BigDecimal.valueOf(asset.averagePurchasePrice()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalInvested = averagePurchasePrice.multiply(asset.count()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal profit = value.subtract(totalInvested).setScale(2, RoundingMode.HALF_UP);
        BigDecimal profitInPercentage = totalInvested.compareTo(BigDecimal.ZERO) <= 0 ? BigDecimal.ZERO :
                profit.multiply(new BigDecimal("100")).divide(totalInvested, 2, RoundingMode.HALF_UP);

        return AssetDTO.builder()
                .name(asset.name())
                .type(type)
                .count(asset.count())
                .currentPrice(finance.price().setScale(2, RoundingMode.HALF_UP))
                .additionCurrency(asset.currency())
                .currency(finance.currency())
                .value(value)
                .averagePurchasePrice(averagePurchasePrice)
                .profit(profit)
                .profitInPercentage(profitInPercentage)
                .build();
    }
}