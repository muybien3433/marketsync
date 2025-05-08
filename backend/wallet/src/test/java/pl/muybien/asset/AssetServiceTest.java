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
import pl.muybien.exception.FinanceNotFoundException;
import pl.muybien.exception.OwnershipException;
import pl.muybien.finance.FinanceClient;
import pl.muybien.finance.FinanceResponse;
import pl.muybien.kafka.confirmation.SubscriptionConfirmation;
import pl.muybien.kafka.confirmation.SupportConfirmation;
import pl.muybien.kafka.producer.SupportProducer;

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

    @Mock
    private SupportProducer support;

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
                "Bitcoin",
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
                "Bitcoin",
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
                "Bitcoin",
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
                "Bitcoin",
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
                "Bitcoin",
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
    void createAsset_CustomType_GeneratesFinanceResponseWithoutClientCall() {
        AssetRequest request = new AssetRequest(
                AssetType.CUSTOM.name(),
                "  My Gold  ",
                BigDecimal.valueOf(5),
                BigDecimal.valueOf(1000),
                CurrencyType.USD.name(),
                "My Gold",
                UnitType.KG.name(),
                BigDecimal.valueOf(1500),
                "Custom asset"
        );

        assetService.createAsset("customerId", request);

        verify(financeClient, never()).findFinanceByTypeAndUri(any(), any());
        ArgumentCaptor<Asset> captor = ArgumentCaptor.forClass(Asset.class);
        verify(repository).save(captor.capture());

        Asset savedAsset = captor.getValue();
        assertThat(savedAsset.getAssetType()).isEqualTo(AssetType.CUSTOM);
        assertThat(savedAsset.getUri()).isEqualTo("my-gold");
        assertThat(savedAsset.getCurrentPrice()).isEqualTo(BigDecimal.valueOf(1500));
    }

    @Test
    void createAsset_NormalizesUriToLowercaseHyphenated() {
        AssetRequest request = new AssetRequest(
                AssetType.CRYPTO.name(),
                "  BiTcoIn Cash  ",
                BigDecimal.ONE,
                BigDecimal.TEN,
                CurrencyType.USD.name(),
                "Bitcoin Cash",
                UnitType.UNIT.name(),
                null,
                ""
        );

        when(financeClient.findFinanceByTypeAndUri(any(), any()))
                .thenReturn(new FinanceResponse("Bitcoin Cash", "BCH", "bitcoin-cash", "UNIT", "10000", "USD", "CRYPTO", LocalDateTime.now()));

        assetService.createAsset("customerId", request);

        ArgumentCaptor<Asset> captor = ArgumentCaptor.forClass(Asset.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getUri()).isEqualTo("bitcoin-cash");
    }

    @Test
    void updateAsset_CustomType_UpdatesNameUriUnitTypeAndCurrentPrice() {
        Asset customAsset = Asset.builder()
                .id(1L)
                .customerId("customerId")
                .assetType(AssetType.CUSTOM)
                .name("Old Name")
                .uri("old-name")
                .unitType(UnitType.UNIT.name())
                .currentPrice(BigDecimal.TEN)
                .build();

        AssetRequest request = new AssetRequest(
                AssetType.CUSTOM.name(),
                "New Name",
                BigDecimal.ONE,
                BigDecimal.ONE,
                CurrencyType.USD.name(),
                "New Name",
                UnitType.KG.name(),
                BigDecimal.valueOf(20),
                "Updated comment"
        );

        when(repository.findById(1L)).thenReturn(Optional.of(customAsset));

        assetService.updateAsset("customerId", request, 1L);

        assertThat(customAsset.getName()).isEqualTo("New Name");
        assertThat(customAsset.getUri()).isEqualTo("new-name");
        assertThat(customAsset.getUnitType()).isEqualTo(UnitType.KG.name());
        assertThat(customAsset.getCurrentPrice()).isEqualTo(BigDecimal.valueOf(20));
        assertThat(customAsset.getComment()).isEqualTo("Updated comment");
    }

    @Test
    void updateAsset_NonCustomType_IgnoresNameUnitTypeCurrentPrice() {
        Asset cryptoAsset = Asset.builder()
                .id(1L)
                .customerId("customerId")
                .assetType(AssetType.CRYPTO)
                .name("Bitcoin")
                .uri("bitcoin")
                .unitType(UnitType.UNIT.name())
                .currentPrice(null)
                .comment("Original comment")
                .build();

        AssetRequest request = new AssetRequest(
                AssetType.CRYPTO.name(),
                "bitcoin",
                BigDecimal.ONE,
                BigDecimal.ONE,
                CurrencyType.USD.name(),
                "New Name",
                UnitType.KG.name(),
                BigDecimal.valueOf(20),
                null
        );

        when(repository.findById(1L)).thenReturn(Optional.of(cryptoAsset));

        assetService.updateAsset("customerId", request, 1L);

        assertThat(cryptoAsset.getName()).isEqualTo("Bitcoin");
        assertThat(cryptoAsset.getUnitType()).isEqualTo(UnitType.UNIT.name());
        assertThat(cryptoAsset.getCurrentPrice()).isNull();
        assertThat(cryptoAsset.getComment()).isEqualTo("Original comment");
    }

    @Test
    void findAllCustomerAssets_CurrencyAsset_ResolvesExchangeRates() {
        AssetGroupDTO currencyGroup = new AssetGroupDTO(
                "EUR",
                "€",
                "eur",
                AssetType.CURRENCY,
                UnitType.UNIT.name(),
                BigDecimal.valueOf(1000),
                new BigDecimal("1.1"),
                null,
                CurrencyType.USD,
                "customerId"
        );

        when(repository.findAndAggregateAssetsByCustomerId("customerId")).thenReturn(Optional.of(List.of(currencyGroup)));
        when(financeClient.findExchangeRate("EUR", "USD")).thenReturn(new BigDecimal("1.2"));
        when(financeClient.findExchangeRate("USD", "EUR")).thenReturn(new BigDecimal("0.85"));

        List<AssetAggregateDTO> result = assetService.findAllCustomerAssets("customerId", "EUR");

        AssetAggregateDTO dto = result.getFirst();
        assertThat(dto.currentPrice()).isEqualTo(new BigDecimal("1.2"));
        assertThat(dto.value()).isEqualTo(new BigDecimal("1200.0"));
        assertThat(dto.exchangeRateToDesired()).isEqualTo(new BigDecimal("0.85"));
    }

    @Test
    void findAllCustomerAssets_FinanceNotFound_SetsCurrentPriceToZero() {
        AssetGroupDTO stockGroup = new AssetGroupDTO(
                "AAPL",
                "AAPL",
                "aapl",
                AssetType.STOCK,
                UnitType.UNIT.name(),
                BigDecimal.TEN,
                new BigDecimal("150"),
                null,
                CurrencyType.USD,
                "customerId"
        );

        when(repository.findAndAggregateAssetsByCustomerId("customerId"))
                .thenReturn(Optional.of(List.of(stockGroup)));

        when(financeClient.findFinanceByTypeAndUri(eq("STOCK"), eq("aapl")))
                .thenThrow(new FinanceNotFoundException("Not found"));

        List<AssetAggregateDTO> result = assetService.findAllCustomerAssets("customerId", "USD");

        AssetAggregateDTO dto = result.getFirst();
        assertThat(dto.currentPrice()).isEqualTo(BigDecimal.ZERO);
        assertThat(dto.value().compareTo(BigDecimal.ZERO)).isZero();
    }

    @Test
    void findAllCustomerAssets_ZeroTotalInvested_ReturnsZeroProfitPercentage() {
        AssetGroupDTO group = new AssetGroupDTO(
                "Gold",
                "",
                "gold",
                AssetType.CUSTOM,
                UnitType.KG.name(),
                BigDecimal.valueOf(5),
                BigDecimal.ZERO,
                new BigDecimal("1500"),
                CurrencyType.USD,
                "customerId"
        );

        when(repository.findAndAggregateAssetsByCustomerId("customerId")).thenReturn(Optional.of(List.of(group)));

        List<AssetAggregateDTO> result = assetService.findAllCustomerAssets("customerId", "USD");

        AssetAggregateDTO dto = result.getFirst();
        assertThat(dto.profitInPercentage()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void findAllCustomerAssets_SameDesiredCurrency_ExchangeRateIsOne() {
        AssetGroupDTO group = new AssetGroupDTO(
                "Bitcoin",
                "BTC",
                "bitcoin",
                AssetType.CRYPTO,
                UnitType.UNIT.name(),
                BigDecimal.ONE,
                new BigDecimal("30000"),
                null,
                CurrencyType.USD,
                "customerId"
        );

        when(repository.findAndAggregateAssetsByCustomerId("customerId")).thenReturn(Optional.of(List.of(group)));
        when(financeClient.findFinanceByTypeAndUri("CRYPTO", "bitcoin"))
                .thenReturn(new FinanceResponse("Bitcoin", "BTC", "bitcoin", "UNIT", "35000", "USD", "CRYPTO", LocalDateTime.now()));

        List<AssetAggregateDTO> result = assetService.findAllCustomerAssets("customerId", "USD");

        AssetAggregateDTO dto = result.getFirst();
        assertThat(dto.exchangeRateToDesired()).isEqualTo(BigDecimal.ONE);
    }

    @Test
    void createAsset_NonCustomCurrencyType_ThrowsFinanceNotFoundException() {
        AssetRequest request = new AssetRequest(
                AssetType.STOCK.name(),
                "AAPL",
                BigDecimal.ONE,
                BigDecimal.TEN,
                CurrencyType.USD.name(),
                "Apple Inc.",
                UnitType.UNIT.name(),
                null,
                ""
        );

        when(financeClient.findFinanceByTypeAndUri(eq("STOCK"), eq("AAPL")))
                .thenThrow(new FinanceNotFoundException("Stock not found"));

        assertThatThrownBy(() -> assetService.createAsset("customerId", request))
                .isInstanceOf(FinanceNotFoundException.class)
                .hasMessage("Stock not found");

        verify(repository, never()).save(any());
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

    @Test
    void findAllCustomerAssets_shouldAggregateAssetsWithFallbackFinance() {
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
                .thenThrow(new FinanceNotFoundException("Finance not found"));

        List<AssetAggregateDTO> assets = assetService.findAllCustomerAssets(customerId, currencyType.name());

        assertThat(assets).hasSize(1);
        AssetAggregateDTO asset = assets.getFirst();
        assertEquals(name, asset.name());
        assertEquals(symbol, asset.symbol());
        assertEquals(uri, asset.uri());
        assertEquals(assetType, asset.assetType());
        assertEquals(currencyType, asset.currencyType());
        assertEquals(unitType, asset.unitType());
        assertEquals(String.format("%.2f", BigDecimal.ZERO), String.format("%.2f", asset.currentPrice()));
    }

    @Test
    void findAllCustomerAssets_shouldSendNotificationToSupport() {
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
                .thenThrow(new FinanceNotFoundException("Finance not found"));

        assetService.findAllCustomerAssets(customerId, currencyType.name());

        verify(support, times(1)).sendNotification(any(SupportConfirmation.class));
    }
}