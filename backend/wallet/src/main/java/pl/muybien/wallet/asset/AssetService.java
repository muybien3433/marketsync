package pl.muybien.wallet.asset;

import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.muybien.wallet.asset.dto.AssetAggregateDTO;
import pl.muybien.wallet.asset.dto.AssetGroupDTO;
import pl.muybien.wallet.asset.dto.AssetHistoryDTO;
import pl.muybien.entity.Asset;
import pl.muybien.enumeration.AlertType;
import pl.muybien.enumeration.AssetType;
import pl.muybien.enumeration.CurrencyType;
import pl.muybien.enumeration.TeamType;
import pl.muybien.wallet.exception.AssetNotFoundException;
import pl.muybien.exception.ErrorResponse;
import pl.muybien.wallet.exception.FinanceNotFoundException;
import pl.muybien.wallet.exception.OwnershipException;
import pl.muybien.feign.FinanceClient;
import pl.muybien.response.FinanceResponse;
import pl.muybien.kafka.confirmation.SupportConfirmation;
import pl.muybien.kafka.SupportProducer;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.UUID;
import java.util.stream.Collectors;

import static pl.muybien.util.PriceUtil.normalizePrice;

@Service
@RequiredArgsConstructor
public class AssetService {
    private final AssetRepository repository;
    private final AssetDTOMapper mapper;
    private final FinanceClient financeClient;
    private final SupportProducer support;

    @Transactional
    public void createAsset(UUID customerId, AssetRequest request) {
        AssetType assetType = request.assetType();
        String normalizedUri = request.uri().trim().toLowerCase().replaceAll(" ", "-");

        FinanceResponse finance;
        if (assetType == AssetType.CUSTOM || assetType == AssetType.CURRENCY) {
            finance = new FinanceResponse(
                    request.uri().substring(0, 1).toUpperCase() + request.uri().substring(1),
                    assetType == AssetType.CUSTOM ? "" : CurrencyType.valueOf(request.uri()).getSymbol(),
                    normalizedUri,
                    request.unitType(),
                    request.purchasePrice(),
                    request.currencyType(),
                    assetType,
                    LocalDateTime.now()
            );
        } else {
            /*
              This validates the asset: if the frontend passes incorrect data,
              the system will attempt to override it if finance data is found,
              otherwise, a FinanceNotFoundException will be thrown and terminate method execution.
             */
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
                        .currencyType(request.currencyType())
                        .customerId(customerId)
                        .comment(request.comment())
                        .build()
        );
    }

    @Transactional
    public AssetHistoryDTO updateAsset(UUID customerId, AssetRequest request, UUID assetId) {
        var asset = repository.findById(assetId).orElseThrow(() ->
                new AssetNotFoundException("Asset with ID %s not found".formatted(assetId)));

        boolean customerIsNotOwner = !customerId.equals(asset.getCustomerId());
        if (customerIsNotOwner) {
            throw new OwnershipException("Asset updating failed:: Customer id mismatch");
        }

        asset.setCount(request.count().setScale(12, RoundingMode.HALF_UP));
        asset.setPurchasePrice(request.purchasePrice().setScale(12, RoundingMode.HALF_UP));
        asset.setCurrencyType(request.currencyType());

        if (request.comment() != null) {
            asset.setComment(request.comment());
        }

        boolean isCustomUnitType = asset.getAssetType() == AssetType.CUSTOM;
        if (isCustomUnitType) {
            if (request.name() != null) {
                asset.setName(request.name().trim());
                asset.setUri(request.name().trim().toLowerCase().replaceAll(" ", "-"));
            }
            if (request.unitType() != null) {
                asset.setUnitType(request.unitType());
            }
            if (request.currentPrice() != null) {
                asset.setCurrentPrice(request.currentPrice());
            }
        }

        return mapper.toAssetHistoryDTO(repository.save(asset));
    }

    @Transactional
    public void deleteAsset(UUID customerId, UUID assetId) {
        var asset = repository.findById(assetId).orElseThrow(() ->
                new EntityNotFoundException("Asset with ID: %s not found".formatted(assetId)));

        boolean customerIsNotOwner = !customerId.equals(asset.getCustomerId());
        if (customerIsNotOwner) {
            throw new OwnershipException("Asset deletion failed:: Customer id mismatch");
        }

        repository.delete(asset);
    }

    @Transactional(readOnly = true)
    public List<AssetHistoryDTO> findAllAssetHistory(UUID customerId) {
        var assetHistory = repository.findAssetHistoryByCustomerId(customerId);

        return assetHistory.stream()
                .sorted(Comparator.comparing(AssetHistoryDTO::createdDate).reversed())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AssetAggregateDTO> findAllCustomerAssets(UUID customerId, CurrencyType desiredCurrency) {
        var groupedAssets = repository.findAndAggregateAssetsByCustomerId(customerId).orElse(Collections.emptyList());
        var aggregatedAssets = new ArrayList<AssetAggregateDTO>();

        groupedAssets.forEach(asset -> aggregatedAssets.add(aggregateAsset(asset, desiredCurrency)));

        return aggregatedAssets;
    }

    private AssetAggregateDTO aggregateAsset(AssetGroupDTO asset, CurrencyType desiredCurrency) {
        BigDecimal currentPrice;
        switch (asset.assetType()) {
            case CUSTOM -> currentPrice = asset.currentPrice();
            case CURRENCY -> currentPrice = resolveExchangeRateToDesired(CurrencyType.valueOf(asset.name()), asset.currencyType());
            default -> {
                FinanceResponse finance;
                try {
                    finance = financeClient.findFinanceByTypeAndUri(asset.assetType(), asset.uri());
                    currentPrice = resolvePriceByCurrency(
                            asset.currencyType(), finance.currencyType(), finance.price());
                } catch (FeignException.InternalServerError e) {
                    finance = createFallbackFinance(asset);
                    currentPrice = finance.price();

                    /*
                     * Notify the IT team that finance data is missing for this asset.
                     * Finance data should always be present—otherwise, the asset would not exist in the database.
                     * This issue may occur if the DB cleaner removed the finance record due to outdated data,
                     * leading to an orphaned asset reference.
                     */
                    ErrorResponse error = new ErrorResponse(
                            LocalDateTime.now().toString(),
                            500,
                            e.getMessage(),
                            "INTERNAL_SERVER_ERROR",
                            "/api/v1/wallets/assets/" + desiredCurrency
                    );
                    support.sendNotification(new SupportConfirmation(TeamType.TECHNICS, AlertType.WARNING, error));
                } catch (Exception e) {
                    finance = createFallbackFinance(asset);
                    currentPrice = finance.price();
                    support.sendNotification(new SupportConfirmation(TeamType.TECHNICS, AlertType.WARNING, e));
                }
            }
        }

        BigDecimal value = asset.count().multiply(currentPrice);
        BigDecimal totalInvested = asset.averagePurchasePrice().multiply(asset.count());
        BigDecimal profit = value.subtract(totalInvested);
        BigDecimal profitPercentage = resolveProfitInPercentage(totalInvested, profit);
        BigDecimal exchangeRateToDesired = resolveExchangeRateToDesired(asset.currencyType(), desiredCurrency);

        return new AssetAggregateDTO(
                asset.name(),
                asset.symbol(),
                asset.uri(),
                asset.assetType(),
                asset.unitType(),
                normalizePrice(asset.count()),
                normalizePrice(currentPrice),
                asset.currencyType(),
                normalizePrice(value),
                normalizePrice(asset.averagePurchasePrice()),
                normalizePrice(profit),
                normalizePrice(profitPercentage),
                exchangeRateToDesired
        );
    }

    private FinanceResponse createFallbackFinance(AssetGroupDTO asset) {
        return new FinanceResponse(
                asset.name(),
                asset.symbol(),
                asset.uri(),
                asset.unitType(),
                BigDecimal.ZERO,
                asset.currencyType(),
                asset.assetType(),
                null
        );
    }

    private BigDecimal resolvePriceByCurrency(CurrencyType assetCurrency, CurrencyType desiredCurrency, BigDecimal price) {
        boolean currencyIsDifferent = assetCurrency != desiredCurrency;
        if (currencyIsDifferent) {
            try {
                var exchangeRate = financeClient.findExchangeRate(desiredCurrency, assetCurrency);
                return price.multiply(exchangeRate);
            } catch (FinanceNotFoundException e) {
                return BigDecimal.ZERO;
            }
        }

        return price;
    }

    private BigDecimal resolveProfitInPercentage(BigDecimal totalInvested, BigDecimal profit) {
        return totalInvested.compareTo(BigDecimal.ZERO) > 0
                ? profit.divide(totalInvested, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;
    }

    private BigDecimal resolveExchangeRateToDesired(CurrencyType currency, CurrencyType desiredCurrency) {
        try {
            return currency.equals(desiredCurrency) ?
                    BigDecimal.ONE :
                    financeClient.findExchangeRate(currency, desiredCurrency);
        } catch (FinanceNotFoundException e) {
            return BigDecimal.ZERO;
        }
    }
}