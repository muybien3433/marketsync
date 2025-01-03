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
import java.time.LocalDateTime;
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
                .type(request.type())
                .name(request.uri().toLowerCase())
                .uri(request.uri())
                .count(request.count().setScale(2, RoundingMode.HALF_UP))
                .purchasePrice(request.purchasePrice().setScale(2, RoundingMode.HALF_UP))
                .customerId(customerId)
                .createdDate(LocalDateTime.now())
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
    List<AssetDTO> findAllCustomerAssets(String authHeader) {
        var customerId = customerClient.fetchCustomerFromHeader(authHeader).id();

        List<AssetGroupDTO> groupedAssets = repository.findAndAggregateAssetsByCustomerId(customerId);
        if (groupedAssets.isEmpty()) {
            return Collections.emptyList();
        }

        var aggregatedAssets = new ArrayList<AssetDTO>();
        groupedAssets.forEach(asset -> aggregatedAssets.add(aggregateAsset(asset)));

        return aggregatedAssets;
    }

    private AssetDTO aggregateAsset(AssetGroupDTO asset) {
        String type = asset.type().name().toLowerCase();
        BigDecimal currentPrice = financeClient.findFinanceByUri(asset.uri()).price().setScale(2, RoundingMode.HALF_UP);
        BigDecimal value = asset.count().multiply(currentPrice).setScale(2, RoundingMode.HALF_UP);
        BigDecimal averagePurchasePrice = BigDecimal.valueOf(asset.averagePurchasePrice()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalInvested = averagePurchasePrice.multiply(asset.count()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal profit = value.subtract(totalInvested).setScale(2, RoundingMode.HALF_UP);
        BigDecimal profitInPercentage = totalInvested.compareTo(BigDecimal.ZERO) <= 0 ? BigDecimal.ZERO :
                profit.multiply(new BigDecimal("100")).divide(totalInvested, 2, RoundingMode.HALF_UP);

        return AssetDTO.builder()
                .id(asset.id())
                .name(asset.name())
                .type(type)
                .count(asset.count())
                .currentPrice(currentPrice)
                .currency(asset.currency())
                .value(value)
                .averagePurchasePrice(averagePurchasePrice)
                .profit(profit)
                .profitInPercentage(profitInPercentage)
                .build();
    }
}