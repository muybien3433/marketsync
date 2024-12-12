package pl.muybien.wallet.asset;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.muybien.wallet.customer.CustomerClient;
import pl.muybien.wallet.exception.CustomerNotFoundException;
import pl.muybien.wallet.exception.OwnershipException;
import pl.muybien.wallet.wallet.WalletService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AssetService {

    private final WalletService walletService;
    private final CustomerClient customerClient;
    private final AssetRepository assetRepository;

    @Transactional
    protected void createAsset(String authHeader, AssetRequest request) {
        var customer = customerClient.fetchCustomerFromHeader(authHeader);
        if (customer == null) {
            throw new CustomerNotFoundException("Customer not found");
        }
        var wallet = walletService.findCustomerWallet(authHeader);
        var asset = Asset.builder()
                .type(request.assetType())
                .name(request.uri().toLowerCase())
                .count(request.count())
                .purchasePrice(request.purchasePrice())
                .customerId(customer.id())
                .createdDate(LocalDateTime.now())
                .wallet(wallet)
                .build();

        assetRepository.save(asset);
    }

    @Transactional
    protected void deleteAsset(String authHeader, Long assetId) {
        var customer = customerClient.fetchCustomerFromHeader(authHeader);
        if (customer == null) {
            throw new CustomerNotFoundException("Customer not found");
        }
        var asset = assetRepository.findById(assetId).orElseThrow(() ->
                new EntityNotFoundException("Asset with ID: %s not found".formatted(assetId)));

        if (!asset.getCustomerId().equals(customer.id())) {
            throw new OwnershipException("Asset deletion failed:: Customer id mismatch");
        }
        assetRepository.delete(asset);
    }
}
