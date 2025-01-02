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
    private static final String customerId = "test123";
    private static final Long assetId = 1L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createAsset_shouldCreateAssetSuccessfully() {
        var assetRequest = new AssetRequest(AssetType.CRYPTO, uri, new BigDecimal("11.00"), new BigDecimal("1000.00"));
        var customer = new CustomerResponse(customerId, "John", "Doe", email);
        var wallet = new Wallet();

        when(customerClient.fetchCustomerFromHeader(authHeader)).thenReturn(customer);
        when(walletService.findCustomerWallet(authHeader)).thenReturn(wallet);

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
        var assetRequest = new AssetRequest(AssetType.CRYPTO, uri, BigDecimal.valueOf(11.00), BigDecimal.valueOf(1_000));

        when(customerClient.fetchCustomerFromHeader(authHeader)).thenReturn(null);

        CustomerNotFoundException exception = assertThrows(CustomerNotFoundException.class, () ->
                service.createAsset(authHeader, assetRequest));

        assertEquals("Customer not found", exception.getMessage());
    }

    @Test
    void deleteAsset_shouldDeleteAssetSuccessfully() {
        var customer = new CustomerResponse(customerId, "John", "Doe", email);
        var asset = Asset.builder()
                .id(assetId)
                .customerId(customerId)
                .build();

        when(customerClient.fetchCustomerFromHeader(authHeader)).thenReturn(customer);
        when(assetRepository.findById(assetId)).thenReturn(Optional.of(asset));

        service.deleteAsset(authHeader, asset.getId());

        verify(assetRepository, times(1)).delete(asset);
    }

    @Test
    void deleteAsset_shouldThrowOwnershipException_whenCustomerIdMismatch() {
        String otherCustomerId = "wrong123";
        var customer = new CustomerResponse(customerId, "John", "Doe", email);
        var anotherAsset = Asset.builder()
                .id(assetId)
                .customerId(otherCustomerId)
                .build();

        when(customerClient.fetchCustomerFromHeader(authHeader)).thenReturn(customer);
        when(assetRepository.findById(assetId)).thenReturn(Optional.of(anotherAsset));

        OwnershipException exception = assertThrows(OwnershipException.class, () ->
                service.deleteAsset(authHeader, assetId));

        assertEquals("Asset deletion failed:: Customer id mismatch", exception.getMessage());
    }

    @Test
    void deleteAsset_shouldThrowCustomerNotFoundException_whenCustomerNotFound() {
        when(customerClient.fetchCustomerFromHeader(authHeader)).thenReturn(null);

        CustomerNotFoundException exception = assertThrows(CustomerNotFoundException.class, () ->
                service.deleteAsset(authHeader, assetId));

        assertEquals("Customer not found", exception.getMessage());
    }

    @Test
    void deleteAsset_shouldThrowEntityNotFoundException_whenAssetNotFound() {
        var customer = new CustomerResponse(customerId, "John", "Doe", email);

        when(customerClient.fetchCustomerFromHeader(authHeader)).thenReturn(customer);
        when(assetRepository.findById(assetId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                service.deleteAsset(authHeader, assetId));

        assertEquals("Asset with ID: 1 not found", exception.getMessage());
    }
}