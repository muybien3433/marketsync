package pl.muybien.wallet.wallet;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.muybien.wallet.asset.Asset;
import pl.muybien.wallet.asset.AssetDTO;
import pl.muybien.wallet.asset.AssetType;
import pl.muybien.wallet.customer.CustomerClient;
import pl.muybien.wallet.exception.*;
import pl.muybien.wallet.finance.FinanceClient;
import pl.muybien.wallet.finance.FinanceResponse;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final FinanceClient financeClient;
    private final CustomerClient customerClient;
    private final WalletRepository walletRepository;

    @Transactional
    List<AssetDTO> displayOrCreateWallet(String authHeader) {
        var customer = customerClient.fetchCustomerFromHeader(authHeader);
        if (customer == null) {
            throw new CustomerNotFoundException("Customer not found");
        }
        var wallet = findOrCreateWallet(customer.id());

        return findAndAggregateAllWalletAssets(wallet, customer.id());
    }

    private synchronized Wallet findOrCreateWallet(String customerId) {
        Optional<Wallet> existingWallet = walletRepository.findByCustomerId(customerId);
        if (existingWallet.isPresent()) {
            return existingWallet.get();
        } else {
            Wallet newWallet = new Wallet();
            newWallet.setCustomerId(customerId);
            return walletRepository.save(newWallet);
        }
    }

    public Wallet findCustomerWallet(String authHeader) {
        var customer = customerClient.fetchCustomerFromHeader(authHeader);
        if (customer == null) {
            throw new CustomerNotFoundException("Customer not found");
        }
        return walletRepository.findByCustomerId(customer.id()).orElseThrow(() ->
                new WalletNotFoundException("Wallet not found"));
    }

    @Transactional(readOnly = true)
    List<AssetDTO> findWalletAssetsHistory(String authHeader) {
        var customer = customerClient.fetchCustomerFromHeader(authHeader);
        if (customer == null) {
            throw new CustomerNotFoundException("Customer not found");
        }
        var wallet = findOrCreateWallet(customer.id());

        if (wallet.getAssets() == null) {
            return Collections.emptyList();
        }

        if (!wallet.getCustomerId().equals(customer.id())) {
            throw new OwnershipException("Wallet displaying failed:: Customer id mismatch");
        }

        return wallet.getAssets().reversed().stream().map(asset -> AssetDTO.builder()
                .id(asset.getId())
                .type(asset.getType().name().toLowerCase())
                .name(asset.getName())
                .count(asset.getCount())
                .averagePurchasePrice(asset.getPurchasePrice())
                .investmentStartDate(asset.getCreatedDate().toLocalDate())
                .build()).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    List<AssetDTO> findAndAggregateAllWalletAssets(Wallet wallet, String customerId) {
        if (wallet.getAssets() == null) {
            return Collections.emptyList();
        }

        if (!wallet.getCustomerId().equals(customerId)) {
            throw new OwnershipException("Wallet displaying failed:: Customer id mismatch");
        }

        return wallet.getAssets().stream()
                .collect(Collectors.groupingBy(Asset::getName))
                .entrySet().stream()
                .map(entry -> {
                    String name = entry.getKey();
                    List<Asset> group = entry.getValue();

                    AssetType type = group.stream()
                            .map(Asset::getType)
                            .findFirst()
                            .orElse(AssetType.UNDEFINED);

                    BigDecimal totalCount = group.stream()
                            .map(Asset::getCount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    BigDecimal totalWeightedPurchasePrice = group.stream()
                            .map(asset -> asset.getPurchasePrice().multiply(asset.getCount()))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    BigDecimal totalAveragePurchasePrice = totalCount.compareTo(BigDecimal.ZERO) > 0
                            ? totalWeightedPurchasePrice.divide(totalCount, RoundingMode.HALF_UP)
                            .setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;

                    LocalDate investmentStartDate = group.stream()
                            .map(asset -> asset.getCreatedDate().toLocalDate())
                            .min(LocalDate::compareTo)
                            .orElse(null);

                    var finance = fetchFinance(name);
                    BigDecimal currentPrice = finance.priceUsd().setScale(2, RoundingMode.HALF_UP);
                    BigDecimal value = totalCount.multiply(currentPrice).setScale(2, RoundingMode.HALF_UP);
                    BigDecimal totalInvested = totalAveragePurchasePrice.multiply(totalCount).setScale(2, RoundingMode.HALF_UP);
                    BigDecimal profit = value.subtract(totalInvested).setScale(2, RoundingMode.HALF_UP);
                    BigDecimal profitInPercentage = totalInvested.setScale(2, RoundingMode.HALF_UP).compareTo(BigDecimal.ZERO) > 0
                            ? profit.multiply(BigDecimal.valueOf(100)).divide(totalInvested, RoundingMode.HALF_UP)
                            : BigDecimal.ZERO;

                    return AssetDTO.builder()
                            .name(name)
                            .type(type.name().toLowerCase())
                            .count(totalCount)
                            .averagePurchasePrice(totalAveragePurchasePrice)
                            .currentPrice(currentPrice)
                            .investmentStartDate(investmentStartDate)
                            .value(value)
                            .profit(profit)
                            .profitInPercentage(profitInPercentage)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Transactional
    protected FinanceResponse fetchFinance(String uri) {
        var finance = financeClient.findFinanceByUri(uri);
        if (finance == null) {
            throw new FinanceNotFoundException("Finance not found for URI: " + uri);
        }
        return FinanceResponse.builder()
                .name(finance.name())
                .priceUsd(finance.priceUsd())
                .build();
    }
}