package pl.muybien.wallet.asset;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.muybien.wallet.customer.CustomerClient;
import pl.muybien.wallet.exception.OwnershipException;
import pl.muybien.wallet.exception.BusinessException;
import pl.muybien.wallet.wallet.WalletService;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AssetService {

    private final WalletService walletService;
    private final CustomerClient customerClient;
    private final AssetRepository assetRepository;

    @Transactional
    protected void createAsset(AssetRequest request, String authorizationHeader) {
        var customer = customerClient.findCustomerById(authorizationHeader, request.customerId())
                .orElseThrow(() -> new BusinessException(
                        "Asset not created:: No Customer exists with ID: %d".formatted(request.customerId())));

        var wallet = walletService.findWalletByCustomerId(customer.id());
        var asset = Asset.builder()
                .name(request.uri().toLowerCase())
                .count(request.count())
                .purchasePrice(request.purchasePrice())
                .investmentStartDate(LocalDate.now())
                .wallet(wallet)
                .customerId(customer.id())
                .createdDate(LocalDateTime.now())
                .build();

        assetRepository.save(asset);
    }

    @Transactional
    protected void deleteAsset(AssetDeletionRequest request, String authorizationHeader) {
        var customer = customerClient.findCustomerById(authorizationHeader, request.customerId())
                .orElseThrow(() -> new BusinessException(
                        "Asset not deleted:: No Customer exists with ID: %d".formatted(request.customerId())));

        var asset = assetRepository.findById(request.assetId()).orElseThrow(() ->
                new EntityNotFoundException("Asset with ID: %s not found".formatted(request.assetId())));

        if (asset.getCustomerId().equals(customer.id())) {
            throw new OwnershipException("Asset deletion failed:: Customer id mismatch");
        }
        assetRepository.delete(asset);
    }
}