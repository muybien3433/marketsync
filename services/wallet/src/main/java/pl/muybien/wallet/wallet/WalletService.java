package pl.muybien.wallet.wallet;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.muybien.wallet.asset.Asset;
import pl.muybien.wallet.asset.AssetDTO;
import pl.muybien.wallet.customer.CustomerClient;
import pl.muybien.wallet.exception.CustomerNotFoundException;
import pl.muybien.wallet.exception.FinanceNotFoundException;
import pl.muybien.wallet.exception.OwnershipException;
import pl.muybien.wallet.exception.WalletCreationException;
import pl.muybien.wallet.finance.FinanceClient;
import pl.muybien.wallet.finance.FinanceResponse;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final FinanceClient financeClient;
    private final CustomerClient customerClient;
    private final WalletRepository walletRepository;

    @Transactional
    protected List<AssetDTO> displayOrCreateWallet(String authHeader, WalletRequest request) {
        var customer = customerClient.findCustomerById(authHeader, request.customerId())
                .orElseThrow(() -> new CustomerNotFoundException(
                        "Wallet not shown:: No Customer exists with ID: %d".formatted(request.customerId())));
        var wallet = findOrCreateWallet(customer.id());

        return findAndAggregateAllWalletAssets(wallet, request);
    }

    @Transactional
    protected Wallet findOrCreateWallet(Long customerId) {
        try {
            return findWalletByCustomerId(customerId);
        } catch (EntityNotFoundException e) {
            return walletRepository.save(Wallet.builder()
                    .customerId(customerId)
                    .createdDate(LocalDateTime.now())
                    .build());
        } catch (Exception e) {
            throw new WalletCreationException("Wallet could not be created");
        }
    }

    @Transactional(readOnly = true)
    public Wallet findWalletByCustomerId(Long customerId) {
        return walletRepository.findByCustomerId(customerId).orElseThrow(() ->
                new EntityNotFoundException("Wallet with customerId: %d not found".formatted(customerId)));
    }

    @Transactional(readOnly = true)
    protected List<AssetDTO> findAndAggregateAllWalletAssets(Wallet wallet, WalletRequest request) {
        if (wallet.getAssets() == null) {
            return Collections.emptyList();
        }

        if (!wallet.getCustomerId().equals(request.customerId())) {
            throw new OwnershipException("Wallet displaying failed:: Customer id mismatch");
        }

        return wallet.getAssets().stream()
                .collect(Collectors.groupingBy(Asset::getName))
                .entrySet().stream()
                .map(entry -> {
                    String name = entry.getKey();
                    List<Asset> group = entry.getValue();

                    BigDecimal totalCount = group.stream()
                            .map(Asset::getCount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    BigDecimal totalPurchasePrice = group.stream()
                            .map(Asset::getPurchasePrice)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    BigDecimal totalAveragePurchasePrice = totalPurchasePrice.divide(
                            BigDecimal.valueOf(group.size()), RoundingMode.HALF_UP);

                    LocalDate investmentStartDate = group.stream()
                            .map(Asset::getInvestmentStartDate)
                            .min(LocalDate::compareTo)
                            .orElse(null);

                    var finance = fetchFinance(name);
                    BigDecimal currentPrice = finance.priceUsd();
                    BigDecimal value = totalCount.multiply(currentPrice);
                    BigDecimal totalInvested = totalAveragePurchasePrice.multiply(totalCount);
                    BigDecimal profit = value.subtract(totalInvested);
                    BigDecimal profitInPercentage = totalInvested.compareTo(BigDecimal.ZERO) > 0
                            ? profit.multiply(BigDecimal.valueOf(100)).divide(totalInvested, RoundingMode.HALF_UP)
                            : BigDecimal.ZERO;

                    return AssetDTO.builder()
                            .name(name)
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
        FinanceResponse finance = financeClient.findFinanceByUri(uri).orElseThrow(() ->
                new FinanceNotFoundException("Finance not found for URI: %s".formatted(uri)));

        return FinanceResponse.builder()
                .name(finance.name())
                .priceUsd(finance.priceUsd())
                .build();
    }

    @Transactional
    protected void deleteWallet(String authHeader, WalletRequest request) {
        var wallet = findWalletByCustomerId(request.customerId());

        var customer = customerClient.findCustomerById(authHeader, request.customerId())
                .orElseThrow(() -> new CustomerNotFoundException(
                        "Wallet not deleted:: No Customer exists with ID: %d".formatted(request.customerId())));


        if (!wallet.getCustomerId().equals(customer.id())) {
            throw new OwnershipException("Wallet deletion failed:: Customer id mismatch");
        }
        walletRepository.delete(wallet);
    }
}