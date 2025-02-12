package pl.muybien.asset;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.muybien.asset.dto.AssetAggregateDTO;
import pl.muybien.asset.dto.AssetGroupDTO;
import pl.muybien.asset.dto.AssetHistoryDTO;
import pl.muybien.customer.CustomerClient;
import pl.muybien.customer.CustomerResponse;
import pl.muybien.exception.AssetNotFoundException;
import pl.muybien.exception.OwnershipException;
import pl.muybien.finance.FinanceClient;
import pl.muybien.finance.FinanceResponse;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AssetServiceTest {

    @Mock
    private CustomerClient customerClient;

    @Mock
    private AssetRepository repository;

    @Mock
    private FinanceClient financeClient;

    @InjectMocks
    private AssetService assetService;

    private CustomerResponse customer;
    private Asset asset;
    private final String authHeader = "Bearer token";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        customer = CustomerResponse.builder()
                .id("customerId")
                .firstName("Joe")
                .lastName("Doe")
                .email("joe.doe@example.com")
                .build();

        asset = Asset.builder()
                .id(1L)
                .customerId(customer.id())
                .build();
    }

    @Test
    void createAsset_shouldSaveAsset() {
        String currency = "USD";
        AssetRequest request = new AssetRequest(
                "cryptos", "bitcoin", BigDecimal.valueOf(2), BigDecimal.valueOf(30000), currency);

        when(customerClient.fetchCustomerFromHeader(authHeader))
                .thenReturn(new CustomerResponse(
                        "customerId", "Joe", "Doe", "joe.doe@example.com"));

        when(financeClient.findFinanceByTypeAndUri(request.assetType(), request.uri()))
                .thenReturn(new FinanceResponse("Bitcoin", "BTC", BigDecimal.valueOf(100_000), "USD", "cryptos"));

        assetService.createAsset(authHeader, request);

        ArgumentCaptor<Asset> assetCaptor = ArgumentCaptor.forClass(Asset.class);
        verify(repository).save(assetCaptor.capture());
        Asset capturedAsset = assetCaptor.getValue();

        assertThat(capturedAsset.getAssetType()).isEqualTo(AssetType.CRYPTOS);
        assertThat(capturedAsset.getName()).isEqualTo("Bitcoin");
        assertThat(capturedAsset.getCount()).isEqualTo(BigDecimal.valueOf(2).setScale(2, RoundingMode.HALF_UP));
        assertThat(capturedAsset.getPurchasePrice()).isEqualTo(BigDecimal.valueOf(30000).setScale(2, RoundingMode.HALF_UP));
    }

    @Test
    void updateAsset_shouldUpdateAsset() {
        String currency = "USD";
        AssetRequest request = new AssetRequest(
                "cryptos", "bitcoin", BigDecimal.valueOf(5), BigDecimal.valueOf(50000), currency);

        when(customerClient.fetchCustomerFromHeader(authHeader)).thenReturn(customer);
        when(repository.findById(asset.getId())).thenReturn(Optional.of(asset));

        assetService.updateAsset(authHeader, request, asset.getId());

        assertThat(asset.getCount()).isEqualTo(BigDecimal.valueOf(5).setScale(2, RoundingMode.HALF_UP));
        assertThat(asset.getPurchasePrice()).isEqualTo(BigDecimal.valueOf(50000).setScale(2, RoundingMode.HALF_UP));

        ArgumentCaptor<Asset> assetCaptor = ArgumentCaptor.forClass(Asset.class);
        verify(repository).save(assetCaptor.capture());
        Asset savedAsset = assetCaptor.getValue();

        assertThat(savedAsset.getCount()).isEqualTo(BigDecimal.valueOf(5).setScale(2, RoundingMode.HALF_UP));
        assertThat(savedAsset.getPurchasePrice()).isEqualTo(BigDecimal.valueOf(50000).setScale(2, RoundingMode.HALF_UP));
    }

    @Test
    void updateAsset_shouldThrowAssetNotFoundException_whenAssetNotFound() {
        String currency = "USD";
        var request = new AssetRequest(
                "cryptos", "bitcoin", BigDecimal.valueOf(5), BigDecimal.valueOf(50000), currency);

        when(customerClient.fetchCustomerFromHeader(authHeader)).thenReturn(customer);
        when(repository.findById(asset.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> assetService.updateAsset(authHeader, request, asset.getId()))
                .isInstanceOf(AssetNotFoundException.class)
                .hasMessage("Asset with ID 1 not found");

        verify(repository, never()).save(any());
    }

    @Test
    void updateAsset_shouldThrowOwnershipException_whenCustomerNotOwner() {
        String currency = "USD";

        var request = new AssetRequest(
                "cryptos", "bitcoin", BigDecimal.valueOf(5), BigDecimal.valueOf(50000), currency);
        asset.setCustomerId("differentCustomerId");

        when(customerClient.fetchCustomerFromHeader(authHeader)).thenReturn(customer);
        when(repository.findById(asset.getId())).thenReturn(Optional.of(asset));

        assertThatThrownBy(() -> assetService.updateAsset(authHeader, request, asset.getId()))
                .isInstanceOf(OwnershipException.class)
                .hasMessage("Asset updating failed:: Customer id mismatch");

        verify(repository, never()).save(any());
    }

    @Test
    void updateAsset_shouldThrowOwnershipExceptionIfCustomerMismatch() {
        String currency = "USD";
        var request = new AssetRequest(
                "cryptos", "bitcoin", BigDecimal.valueOf(3), BigDecimal.valueOf(35000), currency);
        asset.setCustomerId("differentCustomerId");

        when(customerClient.fetchCustomerFromHeader(authHeader)).thenReturn(customer);
        when(repository.findById(asset.getId())).thenReturn(Optional.of(asset));

        assertThatThrownBy(() -> assetService.updateAsset(authHeader, request, asset.getId()))
                .isInstanceOf(OwnershipException.class)
                .hasMessage("Asset updating failed:: Customer id mismatch");
    }

    @Test
    void deleteAsset_shouldThrowEntityNotFoundExceptionIfAssetNotFound() {
        String authHeader = "Bearer token";

        when(customerClient.fetchCustomerFromHeader(authHeader)).thenReturn(customer);
        when(repository.findById(asset.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> assetService.deleteAsset(authHeader, asset.getId()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Asset with ID: 1 not found");
    }

    @Test
    void deleteAsset_shouldDeleteAsset() {
        when(customerClient.fetchCustomerFromHeader(authHeader)).thenReturn(customer);
        when(repository.findById(asset.getId())).thenReturn(Optional.of(asset));

        assetService.deleteAsset(authHeader, asset.getId());

        verify(repository).delete(asset);
        verify(repository, times(1)).findById(asset.getId());
    }

    @Test
    void deleteAsset_shouldThrowOwnershipExceptionIfCustomerMismatch() {
        asset.setCustomerId("differentCustomerId");

        when(customerClient.fetchCustomerFromHeader(authHeader)).thenReturn(customer);
        when(repository.findById(asset.getId())).thenReturn(Optional.of(asset));

        assertThatThrownBy(() -> assetService.deleteAsset(authHeader, asset.getId()))
                .isInstanceOf(OwnershipException.class)
                .hasMessage("Asset deletion failed:: Customer id mismatch");
    }

    @Test
    void findAllAssetHistory_shouldReturnEmptyListIfNoHistory() {
        when(customerClient.fetchCustomerFromHeader(authHeader)).thenReturn(customer);
        when(repository.findAssetHistoryByCustomerId(customer.id())).thenReturn(Collections.emptyList());

        List<AssetHistoryDTO> history = assetService.findAllAssetHistory(authHeader);

        assertThat(history).isEmpty();
    }

    @Test
    void findAllCustomerAssets_shouldAggregateAssets() {
        String uri = "ethereum";
        String currency = "USD";
        var assetGroup = new AssetGroupDTO("Ethereum", "ETH", uri,
                AssetType.CRYPTOS, BigDecimal.valueOf(2),
                new BigDecimal("30000.0"), currency, "customerId"
        );

        when(customerClient.fetchCustomerFromHeader(authHeader)).thenReturn(customer);
        when(repository.findAndAggregateAssetsByCustomerId(customer.id())).thenReturn(Optional.of(List.of(assetGroup)));
        when(financeClient.findFinanceByTypeAndUri(AssetType.CRYPTOS.name().toLowerCase(), uri))
                .thenReturn(new FinanceResponse("Ethereum", "ETH", BigDecimal.valueOf(35000), currency, AssetType.CRYPTOS.name()));


        List<AssetAggregateDTO> assets = assetService.findAllCustomerAssets(authHeader, currency);

        assertThat(assets).hasSize(1);
        AssetAggregateDTO asset = assets.getFirst();
        assertThat(asset.name()).isEqualTo("Ethereum");
        assertEquals(String.format("%.2f", BigDecimal.valueOf(35000)), String.format("%.2f", asset.currentPrice()));
    }
}