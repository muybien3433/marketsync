package pl.muybien.asset;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.muybien.asset.dto.AssetAggregateDTO;
import pl.muybien.asset.dto.AssetGroupDTO;
import pl.muybien.asset.dto.AssetHistoryDTO;
import pl.muybien.enums.AssetType;
import pl.muybien.enums.CurrencyType;
import pl.muybien.exception.AssetNotFoundException;
import pl.muybien.exception.OwnershipException;
import pl.muybien.finance.FinanceClient;
import pl.muybien.finance.FinanceResponse;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssetService {

    private final AssetRepository repository;
    private final FinanceClient financeClient;

    @Transactional
    public void createAsset(String customerId, AssetRequest request) {
        AssetType assetType = AssetType.fromString(request.assetType());
        String normalizedUri = request.uri().trim().toLowerCase().replaceAll(" ", "-");

        FinanceResponse finance;
        if (assetType == AssetType.CUSTOM || assetType == AssetType.CURRENCY) {
            finance = new FinanceResponse(
                    request.uri().substring(0, 1).toUpperCase() + request.uri().substring(1),
                    assetType == AssetType.CUSTOM ? "" : CurrencyType.valueOf(request.uri()).getSymbol(),
                    normalizedUri,
                    request.unitType(),
                    request.purchasePrice().toPlainString(),
                    request.currencyType(),
                    assetType.name(),
                    LocalDateTime.now()
            );
        } else {
            finance = financeClient.findFinanceByTypeAndUri(request.assetType(), request.uri());
        }

        repository.save(
                Asset.builder()
                        .assetType(assetType)
                        .unitType(request.unitType())
                        .name(finance.name())
                        .symbol(finance.symbol())
                        .uri(normalizedUri)
                        .count(request.count().setScale(12, RoundingMode.HALF_UP))
                        .purchasePrice(request.purchasePrice().setScale(12, RoundingMode.HALF_UP))
                        .currentPrice(assetType == AssetType.CUSTOM ? request.currentPrice() : null)
                        .currencyType(CurrencyType.valueOf(finance.currencyType()))
                        .customerId(customerId)
                        .comment(request.comment())
                        .build()
        );
    }

    @Transactional
    public AssetHistoryDTO updateAsset(String customerId, AssetRequest request, Long assetId) {
        var asset = repository.findById(assetId).orElseThrow(() ->
                new AssetNotFoundException("Asset with ID %s not found".formatted(assetId)));

        boolean customerIsNotOwner = !customerId.equals(asset.getCustomerId());
        if (customerIsNotOwner) {
            throw new OwnershipException("Asset updating failed:: Customer id mismatch");
        }

        asset.setCount(request.count().setScale(12, RoundingMode.HALF_UP));
        asset.setPurchasePrice(request.purchasePrice().setScale(12, RoundingMode.HALF_UP));
        asset.setCurrencyType(CurrencyType.valueOf(request.currencyType()));

        if (request.comment() != null) {
            asset.setComment(request.comment());
        }

        boolean isCustomUnitType = asset.getAssetType() == AssetType.CUSTOM;
        if (isCustomUnitType) {
            if (request.uri() != null) {
                asset.setUri(request.uri().trim());
            }
            if (request.unitType() != null) {
                asset.setUnitType(request.unitType().trim());
            }
            if (request.currentPrice() != null) {
                asset.setCurrentPrice(request.currentPrice());
            }
        }

        repository.save(asset);
        return new AssetHistoryDTO(
                asset.getId(),
                asset.getName(),
                asset.getUri(),
                asset.getSymbol(),
                asset.getCount(),
                asset.getCurrencyType(),
                asset.getPurchasePrice(),
                asset.getCurrentPrice(),
                asset.getCreatedDate(),
                asset.getAssetType(),
                asset.getUnitType(),
                asset.getComment()
        );
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
        BigDecimal currentPrice;
        switch (asset.assetType()) {
            case CUSTOM -> currentPrice = asset.currentPrice();
            case CURRENCY -> currentPrice = resolveCurrencyPrice(asset.currencyType(), desiredCurrency);
            default -> {
                try {
                    var finance = financeClient.findFinanceByTypeAndUri(asset.assetType().name(), asset.uri());
                    currentPrice = resolvePriceByCurrency(
                            asset.currencyType(), finance.currencyType(), new BigDecimal(finance.price()));
                } catch (Exception e) {
                    currentPrice = BigDecimal.ZERO;
                }
            }
        }

        BigDecimal value = asset.count().multiply(currentPrice);
        BigDecimal totalInvested = resolveTotalInvested(asset, desiredCurrency);
        BigDecimal profit = value.subtract(totalInvested);
        BigDecimal profitPercentage = resolveProfitInPercentage(totalInvested, profit);
        BigDecimal exchangeRateToDesired = resolveExchangeRateToDesired(asset.currencyType().name(), desiredCurrency);

        return new AssetAggregateDTO(
                asset.name(),
                asset.symbol(),
                asset.assetType(),
                asset.unitType(),
                asset.count(),
                currentPrice,
                asset.currencyType(),
                value,
                asset.averagePurchasePrice(),
                profit,
                profitPercentage,
                exchangeRateToDesired
        );
    }

    private BigDecimal resolveCurrencyPrice(CurrencyType assetCurrency, String desiredCurrency) {
        boolean currencyIsDifferent = assetCurrency != CurrencyType.valueOf(desiredCurrency);
        if (currencyIsDifferent) {
            return financeClient.findExchangeRate(desiredCurrency, assetCurrency.name());
        }
        return BigDecimal.ONE;
    }

    private BigDecimal resolvePriceByCurrency(CurrencyType assetCurrency, String desiredCurrency, BigDecimal price) {
        boolean currencyIsDifferent = assetCurrency != CurrencyType.valueOf(desiredCurrency);
        if (currencyIsDifferent) {
            var exchangeRate = financeClient.findExchangeRate(assetCurrency.name(), desiredCurrency);
            return price.multiply(exchangeRate);
        }
        return price;
    }

    private BigDecimal resolveProfitInPercentage(BigDecimal totalInvested, BigDecimal profit) {
        return totalInvested.compareTo(BigDecimal.ZERO) > 0
                ? profit.divide(totalInvested, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;
    }

//    // TODO: This is new method, test it out
//    private BigDecimal resolveValue(AssetGroupDTO asset, String desiredCurrency, BigDecimal currentPrice) {
//        boolean currencyIsSame = asset.currencyType() == CurrencyType.valueOf(desiredCurrency);
//        if (asset.assetType() == AssetType.CURRENCY && currencyIsSame) {
//            return asset.count();
//        }
//        return asset.count().multiply(currentPrice);
//    }

    private BigDecimal resolveTotalInvested(AssetGroupDTO asset, String desiredCurrency) {
        boolean currencyIsSame = asset.currencyType() == CurrencyType.valueOf(desiredCurrency);
        if (asset.assetType() == AssetType.CURRENCY && currencyIsSame) {
            return asset.count();
        }
        return asset.averagePurchasePrice().multiply(asset.count());
    }

    private BigDecimal resolveExchangeRateToDesired(String currency, String desiredCurrency) {
        return currency.equalsIgnoreCase(desiredCurrency) ?
                BigDecimal.ONE :
                financeClient.findExchangeRate(currency, desiredCurrency);
    }
}