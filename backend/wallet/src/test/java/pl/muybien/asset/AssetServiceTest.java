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
import pl.muybien.enums.AssetType;
import pl.muybien.enums.CurrencyType;
import pl.muybien.enums.UnitType;
import pl.muybien.exception.AssetNotFoundException;
import pl.muybien.exception.OwnershipException;
import pl.muybien.finance.FinanceClient;
import pl.muybien.finance.FinanceResponse;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AssetServiceTest {

    @Mock
    private AssetRepository repository;

    @Mock
    private FinanceClient financeClient;

    @InjectMocks
    private AssetService assetService;

    private Asset asset;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        asset = Asset.builder()
                .id(1L)
                .customerId("customerId")
                .build();
    }

    @Test
    void createAsset_shouldSaveAsset() {
        String customerId = "customerId";
        AssetRequest request = new AssetRequest(
                AssetType.CRYPTO.name(),
                "bitcoin",
                BigDecimal.valueOf(2),
                BigDecimal.valueOf(30000),
                CurrencyType.USD.name(),
                UnitType.UNIT.name(),
                null,
                ""

        );

        when(financeClient.findFinanceByTypeAndUri(request.assetType(), request.uri()))
                .thenReturn(new FinanceResponse(
                        "Bitcoin",
                        "BTC",
                        "bitcoin",
                        UnitType.UNIT.name(),
                        "100000",
                        CurrencyType.USD.name(),
                        AssetType.CRYPTO.name(),
                        LocalDateTime.now()));

        assetService.createAsset(customerId, request);

        ArgumentCaptor<Asset> assetCaptor = ArgumentCaptor.forClass(Asset.class);
        verify(repository).save(assetCaptor.capture());
        Asset capturedAsset = assetCaptor.getValue();

        assertThat(capturedAsset.getAssetType()).isEqualTo(AssetType.CRYPTO);
        assertThat(capturedAsset.getName()).isEqualTo("Bitcoin");
        assertThat(capturedAsset.getCount()).isEqualTo(BigDecimal.valueOf(2).setScale(12, RoundingMode.HALF_UP));
        assertThat(capturedAsset.getPurchasePrice()).isEqualTo(BigDecimal.valueOf(30000).setScale(12, RoundingMode.HALF_UP));
    }

    @Test
    void updateAsset_shouldUpdateAsset() {
        String customerId = "customerId";
        AssetRequest request = new AssetRequest(
                AssetType.CRYPTO.name(),
                "bitcoin",
                BigDecimal.valueOf(5),
                BigDecimal.valueOf(50000),
                CurrencyType.USD.name(),
                UnitType.UNIT.name(),
                null,
                ""
        );

        when(repository.findById(asset.getId())).thenReturn(Optional.of(asset));

        assetService.updateAsset(customerId, request, asset.getId());

        assertThat(asset.getCount()).isEqualTo(BigDecimal.valueOf(5).setScale(12, RoundingMode.HALF_UP));
        assertThat(asset.getPurchasePrice()).isEqualTo(BigDecimal.valueOf(50000).setScale(12, RoundingMode.HALF_UP));

        ArgumentCaptor<Asset> assetCaptor = ArgumentCaptor.forClass(Asset.class);
        verify(repository).save(assetCaptor.capture());
        Asset savedAsset = assetCaptor.getValue();

        assertThat(savedAsset.getCount()).isEqualTo(BigDecimal.valueOf(5).setScale(12, RoundingMode.HALF_UP));
        assertThat(savedAsset.getPurchasePrice()).isEqualTo(BigDecimal.valueOf(50000).setScale(12, RoundingMode.HALF_UP));
    }

    @Test
    void updateAsset_shouldThrowAssetNotFoundException_whenAssetNotFound() {
        String customerId = "customerId";
        var request = new AssetRequest(
                AssetType.CRYPTO.name(),
                "bitcoin",
                BigDecimal.valueOf(5),
                BigDecimal.valueOf(50000),
                UnitType.UNIT.name(),
                CurrencyType.USD.name(),
                null,
                ""
                );

        when(repository.findById(asset.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> assetService.updateAsset(customerId, request, asset.getId()))
                .isInstanceOf(AssetNotFoundException.class)
                .hasMessage("Asset with ID 1 not found");

        verify(repository, never()).save(any());
    }

    @Test
    void updateAsset_shouldThrowOwnershipException_whenCustomerNotOwner() {
        String customerId = "customerId";
        var request = new AssetRequest(
                AssetType.CRYPTO.name(),
                "bitcoin",
                BigDecimal.valueOf(5),
                BigDecimal.valueOf(50000),
                UnitType.UNIT.name(),
                CurrencyType.USD.name(),
                null,
                ""
        );

        asset.setCustomerId("differentCustomerId");

        when(repository.findById(asset.getId())).thenReturn(Optional.of(asset));

        assertThatThrownBy(() -> assetService.updateAsset(customerId, request, asset.getId()))
                .isInstanceOf(OwnershipException.class)
                .hasMessage("Asset updating failed:: Customer id mismatch");

        verify(repository, never()).save(any());
    }

    @Test
    void updateAsset_shouldThrowOwnershipExceptionIfCustomerMismatch() {
        String customerId = "customerId";
        var request = new AssetRequest(
                AssetType.CRYPTO.name(),
                "bitcoin",
                BigDecimal.valueOf(3),
                BigDecimal.valueOf(35000),
                UnitType.UNIT.name(),
                CurrencyType.USD.name(),
                null,
                ""
        );
        asset.setCustomerId("differentCustomerId");

        when(repository.findById(asset.getId())).thenReturn(Optional.of(asset));

        assertThatThrownBy(() -> assetService.updateAsset(customerId, request, asset.getId()))
                .isInstanceOf(OwnershipException.class)
                .hasMessage("Asset updating failed:: Customer id mismatch");
    }

    @Test
    void deleteAsset_shouldThrowEntityNotFoundExceptionIfAssetNotFound() {
        String authHeader = "Bearer token";

        when(repository.findById(asset.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> assetService.deleteAsset(authHeader, asset.getId()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Asset with ID: 1 not found");
    }

    @Test
    void deleteAsset_shouldDeleteAsset() {
        String customerId = "customerId";

        when(repository.findById(asset.getId())).thenReturn(Optional.of(asset));

        assetService.deleteAsset(customerId, asset.getId());

        verify(repository).delete(asset);
        verify(repository, times(1)).findById(asset.getId());
    }

    @Test
    void deleteAsset_shouldThrowOwnershipExceptionIfCustomerMismatch() {
        String customerId = "customerId";
        asset.setCustomerId("differentCustomerId");

        when(repository.findById(asset.getId())).thenReturn(Optional.of(asset));

        assertThatThrownBy(() -> assetService.deleteAsset(customerId, asset.getId()))
                .isInstanceOf(OwnershipException.class)
                .hasMessage("Asset deletion failed:: Customer id mismatch");
    }

    @Test
    void findAllAssetHistory_shouldReturnEmptyListIfNoHistory() {
        String customerId = "customerId";

        when(repository.findAssetHistoryByCustomerId(customerId)).thenReturn(Collections.emptyList());

        List<AssetHistoryDTO> history = assetService.findAllAssetHistory(customerId);

        assertThat(history).isEmpty();
    }

    @Test
    void findAllCustomerAssets_shouldAggregateAssets() {
        String customerId = "customerId";
        String name = "Ethereum";
        String symbol = "ETH";
        String uri = "ethereum";
        AssetType assetType = AssetType.CRYPTO;
        CurrencyType currencyType = CurrencyType.USD;
        String unitType = UnitType.UNIT.name();
        var assetGroup = new AssetGroupDTO(
                name,
                symbol,
                uri,
                AssetType.CRYPTO,
                unitType,
                BigDecimal.valueOf(2),
                new BigDecimal("30000.0"),
                null,
                currencyType,
                "customerId"
        );

        when(repository.findAndAggregateAssetsByCustomerId(customerId)).thenReturn(Optional.of(List.of(assetGroup)));
        when(financeClient.findFinanceByTypeAndUri(assetType.name(), uri))
                .thenReturn(new FinanceResponse(
                        name,
                        symbol,
                        uri,
                        unitType,
                        "35000",
                        currencyType.name(),
                        assetType.name(),
                        LocalDateTime.now()));

        List<AssetAggregateDTO> assets = assetService.findAllCustomerAssets(customerId, currencyType.name());

        assertThat(assets).hasSize(1);
        AssetAggregateDTO asset = assets.getFirst();
        assertThat(asset.name()).isEqualTo("Ethereum");
        assertEquals(String.format("%.2f", BigDecimal.valueOf(35000)), String.format("%.2f", asset.currentPrice()));
    }
}