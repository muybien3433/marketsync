package pl.muybien.wallet.asset;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.muybien.wallet.customer.CustomerClient;
import pl.muybien.wallet.customer.CustomerResponse;
import pl.muybien.wallet.exception.CustomerNotFoundException;
import pl.muybien.wallet.exception.OwnershipException;
import pl.muybien.wallet.wallet.Wallet;
import pl.muybien.wallet.wallet.WalletService;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AssetServiceTest {

    @InjectMocks
    private AssetService service;

    @Mock
    private WalletService walletService;

    @Mock
    private CustomerClient customerClient;

    @Mock
    private AssetRepository assetRepository;

    private static final String authHeader = "Bearer token";
    private static final String email = "john.doe@example.com";
    private static final String uri = "Uri";
    private static final Long customerId = 1L;
    private static final Long assetId = 1L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createAsset_shouldCreateAssetSuccessfully() {
        var assetRequest = new AssetRequest(uri, BigDecimal.valueOf(11), BigDecimal.valueOf(1_000), customerId);
        var customer = new CustomerResponse(customerId, "John", "Doe", email);
        var wallet = new Wallet();

        when(customerClient.findCustomerById(authHeader, customerId)).thenReturn(Optional.of(customer));
        when(walletService.findWalletByCustomerId(customerId)).thenReturn(wallet);

        service.createAsset(authHeader, assetRequest);

        ArgumentCaptor<Asset> assetCaptor = ArgumentCaptor.forClass(Asset.class);
        verify(assetRepository, times(1)).save(assetCaptor.capture());

        Asset savedAsset = assetCaptor.getValue();
        assertNotNull(savedAsset);
        assertEquals(assetRequest.uri().toLowerCase(), savedAsset.getName());
        assertEquals(assetRequest.count(), savedAsset.getCount());
        assertEquals(assetRequest.purchasePrice(), savedAsset.getPurchasePrice());
        assertEquals(customerId, savedAsset.getCustomerId());
        assertNotNull(savedAsset.getCreatedDate());
        assertEquals(wallet, savedAsset.getWallet());
    }

    @Test
    void createAsset_shouldThrowCustomerNotFoundException_whenCustomerNotFound() {
        var assetRequest = new AssetRequest(uri, BigDecimal.valueOf(11), BigDecimal.valueOf(1_000), customerId);

        when(customerClient.findCustomerById(authHeader, customerId)).thenReturn(Optional.empty());

        CustomerNotFoundException exception = assertThrows(CustomerNotFoundException.class, () ->
                service.createAsset(authHeader, assetRequest));

        assertEquals("Asset not created:: No Customer exists with ID: 1", exception.getMessage());
    }

    @Test
    void deleteAsset_shouldDeleteAssetSuccessfully() {
        var assetDeletionRequest = new AssetDeletionRequest(assetId, customerId);
        var customer = new CustomerResponse(customerId, "John", "Doe", email);
        var asset = Asset.builder()
                .customerId(customerId)
                .build();

        when(customerClient.findCustomerById(authHeader, customerId)).thenReturn(Optional.of(customer));
        when(assetRepository.findById(assetId)).thenReturn(Optional.of(asset));

        service.deleteAsset(authHeader, assetDeletionRequest);

        verify(assetRepository, times(1)).delete(asset);
    }

    @Test
    void deleteAsset_shouldThrowOwnershipException_whenCustomerIdMismatch() {
        Long otherCustomerId = 2L;
        var assetDeletionRequest = new AssetDeletionRequest(assetId, customerId);
        var customer = new CustomerResponse(customerId, "John", "Doe", email);
        var anotherAsset = Asset.builder()
                .id(assetId)
                .customerId(otherCustomerId)
                .build();

        when(customerClient.findCustomerById(authHeader, customerId)).thenReturn(Optional.of(customer));
        when(assetRepository.findById(assetId)).thenReturn(Optional.of(anotherAsset));

        OwnershipException exception = assertThrows(OwnershipException.class, () ->
                service.deleteAsset(authHeader, assetDeletionRequest));

        assertEquals("Asset deletion failed:: Customer id mismatch", exception.getMessage());
    }

    @Test
    void deleteAsset_shouldThrowCustomerNotFoundException_whenCustomerNotFound() {
        var assetDeletionRequest = new AssetDeletionRequest(assetId, customerId);

        when(customerClient.findCustomerById(authHeader, customerId)).thenReturn(Optional.empty());

        CustomerNotFoundException exception = assertThrows(CustomerNotFoundException.class, () ->
                service.deleteAsset(authHeader, assetDeletionRequest));

        assertEquals("Asset not deleted:: No Customer exists with ID: 1", exception.getMessage());
    }

    @Test
    void deleteAsset_shouldThrowEntityNotFoundException_whenAssetNotFound() {
        var assetDeletionRequest = new AssetDeletionRequest(assetId, customerId);
        var customer = new CustomerResponse(customerId, "John", "Doe", email);

        when(customerClient.findCustomerById(authHeader, customerId)).thenReturn(Optional.of(customer));
        when(assetRepository.findById(assetId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                service.deleteAsset(authHeader, assetDeletionRequest));

        assertEquals("Asset with ID: 1 not found", exception.getMessage());
    }
}