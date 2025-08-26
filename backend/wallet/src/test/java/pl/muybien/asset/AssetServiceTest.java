package pl.muybien.asset;

import feign.FeignException;
import feign.Request;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.muybien.wallet.asset.AssetRepository;
import pl.muybien.wallet.asset.AssetRequest;
import pl.muybien.wallet.asset.AssetService;
import pl.muybien.wallet.asset.dto.AssetAggregateDTO;
import pl.muybien.wallet.asset.dto.AssetGroupDTO;
import pl.muybien.wallet.asset.dto.AssetHistoryDTO;
import pl.muybien.entity.Asset;
import pl.muybien.enumeration.AssetType;
import pl.muybien.enumeration.CurrencyType;
import pl.muybien.enumeration.UnitType;
import pl.muybien.wallet.exception.AssetNotFoundException;
import pl.muybien.wallet.exception.FinanceNotFoundException;
import pl.muybien.wallet.exception.OwnershipException;
import pl.muybien.feign.FinanceClient;
import pl.muybien.response.FinanceResponse;
import pl.muybien.kafka.confirmation.SupportConfirmation;
import pl.muybien.kafka.producer.SupportProducer;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
    private String assetIdString;
    private UUID customerUuid;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        assetIdString = "550e8400-e29b-41d4-a716-446655440000";
        asset = Asset.builder()
                .id(UUID.fromString(assetIdString))
                .customerId(UUID.fromString("550e8400-e29b-41d4-a716-446655440010"))
                .build();
        customerUuid = UUID.fromString("550e8400-e29b-41d4-a716-446655440010");
    }

    @Test
    void createAsset_shouldSaveAsset() {
        UUID customerId = customerUuid;
        AssetRequest request = new AssetRequest(
                AssetType.CRYPTO,
                "bitcoin",
                BigDecimal.valueOf(2),
                BigDecimal.valueOf(30000),
                CurrencyType.USD,
                "Bitcoin",
                UnitType.UNIT,
                null,
                ""

        );

        when(financeClient.findFinanceByTypeAndUri(request.assetType(), request.uri()))
                .thenReturn(new FinanceResponse(
                        "Bitcoin",
                        "BTC",
                        "bitcoin",
                        UnitType.UNIT,
                        "100000",
                        CurrencyType.USD,
                        AssetType.CRYPTO,
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
        UUID customerId = customerUuid;
        AssetRequest request = new AssetRequest(
                AssetType.CRYPTO,
                "bitcoin",
                BigDecimal.valueOf(5),
                BigDecimal.valueOf(50000),
                CurrencyType.USD,
                "Bitcoin",
                UnitType.UNIT,
                null,
                ""
        );

        when(repository.findById(UUID.fromString(assetIdString))).thenReturn(Optional.of(asset));

        assetService.updateAsset(customerId, request, UUID.fromString(assetIdString));

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
        UUID customerId = customerUuid;
        var request = new AssetRequest(
                AssetType.CRYPTO,
                "bitcoin",
                BigDecimal.valueOf(5),
                BigDecimal.valueOf(50000),
                CurrencyType.USD,
                "Bitcoin",
                UnitType.UNIT,
                null,
                ""
        );

        when(repository.findById(UUID.fromString(assetIdString))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> assetService.updateAsset(customerId, request, UUID.fromString(assetIdString)))
                .isInstanceOf(AssetNotFoundException.class)
                .hasMessage("Asset with ID " + assetIdString + " not found");

        verify(repository, never()).save(any());
    }

    @Test
    void updateAsset_shouldThrowOwnershipException_whenCustomerNotOwner() {
        UUID customerId = customerUuid;
        UUID differentCustomerId = UUID.randomUUID();
        var request = new AssetRequest(
                AssetType.CRYPTO,
                "bitcoin",
                BigDecimal.valueOf(5),
                BigDecimal.valueOf(50000),
                CurrencyType.USD,
                "Bitcoin",
                UnitType.UNIT,
                null,
                ""
        );

        asset.setCustomerId(differentCustomerId);

        when(repository.findById(UUID.fromString(assetIdString))).thenReturn(Optional.of(asset));

        assertThatThrownBy(() -> assetService.updateAsset(customerId, request, UUID.fromString(assetIdString)))
                .isInstanceOf(OwnershipException.class)
                .hasMessage("Asset updating failed:: Customer id mismatch");

        verify(repository, never()).save(any());
    }

    @Test
    void updateAsset_shouldThrowOwnershipExceptionIfCustomerMismatch() {
        UUID customerId = customerUuid;
        UUID differentCustomerId =  UUID.randomUUID();
        var request = new AssetRequest(
                AssetType.CRYPTO,
                "bitcoin",
                BigDecimal.valueOf(3),
                BigDecimal.valueOf(35000),
                CurrencyType.USD,
                "Bitcoin",
                UnitType.UNIT,
                null,
                ""
        );
        asset.setCustomerId(differentCustomerId);

        when(repository.findById(UUID.fromString(assetIdString))).thenReturn(Optional.of(asset));

        assertThatThrownBy(() -> assetService.updateAsset(customerId, request, UUID.fromString(assetIdString)))
                .isInstanceOf(OwnershipException.class)
                .hasMessage("Asset updating failed:: Customer id mismatch");
    }

    @Test
    void deleteAsset_shouldThrowEntityNotFoundExceptionIfAssetNotFound() {
        UUID customerId = customerUuid;

        when(repository.findById(UUID.fromString(assetIdString))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> assetService.deleteAsset(customerId, UUID.fromString(assetIdString)))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Asset with ID: " + assetIdString + " not found");
    }

    @Test
    void deleteAsset_shouldDeleteAsset() {
        UUID customerId = customerUuid;

        when(repository.findById(UUID.fromString(assetIdString))).thenReturn(Optional.of(asset));

        assetService.deleteAsset(customerId, UUID.fromString(assetIdString));

        verify(repository).delete(asset);
        verify(repository, times(1)).findById(UUID.fromString(assetIdString));
    }

    @Test
    void deleteAsset_shouldThrowOwnershipExceptionIfCustomerMismatch() {
        UUID customerId = customerUuid;
        UUID differentCustomerId =  UUID.randomUUID();
        asset.setCustomerId(differentCustomerId);

        when(repository.findById(UUID.fromString(assetIdString))).thenReturn(Optional.of(asset));

        assertThatThrownBy(() -> assetService.deleteAsset(customerId, UUID.fromString(assetIdString)))
                .isInstanceOf(OwnershipException.class)
                .hasMessage("Asset deletion failed:: Customer id mismatch");
    }

    @Test
    void findAllAssetHistory_shouldReturnEmptyListIfNoHistory() {
        UUID customerId = customerUuid;

        when(repository.findAssetHistoryByCustomerId(customerId)).thenReturn(Collections.emptyList());

        List<AssetHistoryDTO> history = assetService.findAllAssetHistory(customerId);

        assertThat(history).isEmpty();
    }

    @Test
    void createAsset_CustomType_GeneratesFinanceResponseWithoutClientCall() {
        AssetRequest request = new AssetRequest(
                AssetType.CUSTOM,
                "  My Gold  ",
                BigDecimal.valueOf(5),
                BigDecimal.valueOf(1000),
                CurrencyType.USD,
                "My Gold",
                UnitType.KG,
                BigDecimal.valueOf(1500),
                "Custom asset"
        );

        assetService.createAsset(customerUuid, request);

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
        UUID customerId =  UUID.randomUUID();
        AssetRequest request = new AssetRequest(
                AssetType.CRYPTO,
                "  BiTcoIn Cash  ",
                BigDecimal.ONE,
                BigDecimal.TEN,
                CurrencyType.USD,
                "Bitcoin Cash",
                UnitType.UNIT,
                null,
                ""
        );

        when(financeClient.findFinanceByTypeAndUri(any(), any()))
                .thenReturn(new FinanceResponse("Bitcoin Cash", "BCH", "bitcoin-cash", UnitType.UNIT, "10000", CurrencyType.USD, AssetType.CRYPTO, LocalDateTime.now()));

        assetService.createAsset(customerId, request);

        ArgumentCaptor<Asset> captor = ArgumentCaptor.forClass(Asset.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getUri()).isEqualTo("bitcoin-cash");
    }

    @Test
    void updateAsset_CustomType_UpdatesNameUriUnitTypeAndCurrentPrice() {
        UUID customAssetId = UUID.randomUUID();
        Asset customAsset = Asset.builder()
                .id(customAssetId)
                .customerId(customerUuid)
                .assetType(AssetType.CUSTOM)
                .name("Old Name")
                .uri("old-name")
                .unitType(UnitType.UNIT)
                .currentPrice(BigDecimal.TEN)
                .build();

        AssetRequest request = new AssetRequest(
                AssetType.CUSTOM,
                "New Name",
                BigDecimal.ONE,
                BigDecimal.ONE,
                CurrencyType.USD,
                "New Name",
                UnitType.KG,
                BigDecimal.valueOf(20),
                "Updated comment"
        );

        when(repository.findById(customAssetId)).thenReturn(Optional.of(customAsset));

        assetService.updateAsset(customerUuid, request, customAssetId);

        assertThat(customAsset.getName()).isEqualTo("New Name");
        assertThat(customAsset.getUri()).isEqualTo("new-name");
        assertThat(customAsset.getUnitType()).isEqualTo(UnitType.KG);
        assertThat(customAsset.getCurrentPrice()).isEqualTo(BigDecimal.valueOf(20));
        assertThat(customAsset.getComment()).isEqualTo("Updated comment");
    }

    @Test
    void updateAsset_NonCustomType_IgnoresNameUnitTypeCurrentPrice() {
        UUID cryptoAssetId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        Asset cryptoAsset = Asset.builder()
                .id(cryptoAssetId)
                .customerId(customerId)
                .assetType(AssetType.CRYPTO)
                .name("Bitcoin")
                .uri("bitcoin")
                .unitType(UnitType.UNIT)
                .currentPrice(null)
                .comment("Original comment")
                .build();

        AssetRequest request = new AssetRequest(
                AssetType.CRYPTO,
                "bitcoin",
                BigDecimal.ONE,
                BigDecimal.ONE,
                CurrencyType.USD,
                "New Name",
                UnitType.KG,
                BigDecimal.valueOf(20),
                null
        );

        when(repository.findById(cryptoAssetId)).thenReturn(Optional.of(cryptoAsset));

        assetService.updateAsset(customerUuid, request, cryptoAssetId);

        assertThat(cryptoAsset.getName()).isEqualTo("Bitcoin");
        assertThat(cryptoAsset.getUnitType()).isEqualTo(UnitType.UNIT);
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
                UnitType.UNIT,
                BigDecimal.valueOf(1000),
                new BigDecimal("1.1"),
                null,
                CurrencyType.USD,
                customerUuid
        );

        when(repository.findAndAggregateAssetsByCustomerId(customerUuid)).thenReturn(Optional.of(List.of(currencyGroup)));
        when(financeClient.findExchangeRate(CurrencyType.EUR, CurrencyType.USD)).thenReturn(new BigDecimal("1.2"));
        when(financeClient.findExchangeRate(CurrencyType.USD, CurrencyType.EUR)).thenReturn(new BigDecimal("0.85"));

        List<AssetAggregateDTO> result = assetService.findAllCustomerAssets(customerUuid, CurrencyType.EUR);

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
                UnitType.UNIT,
                BigDecimal.TEN,
                new BigDecimal("150"),
                null,
                CurrencyType.USD,
                customerUuid
        );

        when(repository.findAndAggregateAssetsByCustomerId(customerUuid))
                .thenReturn(Optional.of(List.of(stockGroup)));

        when(financeClient.findFinanceByTypeAndUri(eq(AssetType.STOCK), eq("aapl")))
                .thenThrow(new FinanceNotFoundException("Not found"));

        List<AssetAggregateDTO> result = assetService.findAllCustomerAssets(customerUuid, CurrencyType.USD);

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
                UnitType.KG,
                BigDecimal.valueOf(5),
                BigDecimal.ZERO,
                new BigDecimal("1500"),
                CurrencyType.USD,
                customerUuid
        );

        when(repository.findAndAggregateAssetsByCustomerId(customerUuid)).thenReturn(Optional.of(List.of(group)));

        List<AssetAggregateDTO> result = assetService.findAllCustomerAssets(customerUuid, CurrencyType.USD);

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
                UnitType.UNIT,
                BigDecimal.ONE,
                new BigDecimal("30000"),
                null,
                CurrencyType.USD,
                customerUuid
        );

        when(repository.findAndAggregateAssetsByCustomerId(customerUuid)).thenReturn(Optional.of(List.of(group)));
        when(financeClient.findFinanceByTypeAndUri(AssetType.CRYPTO, "bitcoin"))
                .thenReturn(new FinanceResponse("Bitcoin", "BTC", "bitcoin", UnitType.UNIT, "35000", CurrencyType.USD, AssetType.CRYPTO, LocalDateTime.now()));

        List<AssetAggregateDTO> result = assetService.findAllCustomerAssets(customerUuid, CurrencyType.USD);

        AssetAggregateDTO dto = result.getFirst();
        assertThat(dto.exchangeRateToDesired()).isEqualTo(BigDecimal.ONE);
    }

    @Test
    void createAsset_NonCustomCurrencyType_ThrowsFinanceNotFoundException() {
        AssetRequest request = new AssetRequest(
                AssetType.STOCK,
                "AAPL",
                BigDecimal.ONE,
                BigDecimal.TEN,
                CurrencyType.USD,
                "Apple Inc.",
                UnitType.UNIT,
                null,
                ""
        );

        when(financeClient.findFinanceByTypeAndUri(eq(AssetType.STOCK), eq("AAPL")))
                .thenThrow(new FinanceNotFoundException("Stock not found"));

        assertThatThrownBy(() -> assetService.createAsset(customerUuid, request))
                .isInstanceOf(FinanceNotFoundException.class)
                .hasMessage("Stock not found");

        verify(repository, never()).save(any());
    }

    @Test
    void findAllCustomerAssets_shouldAggregateAssets() {
        UUID customerId = UUID.randomUUID();
        String name = "Ethereum";
        String symbol = "ETH";
        String uri = "ethereum";
        AssetType assetType = AssetType.CRYPTO;
        CurrencyType currencyType = CurrencyType.USD;
        UnitType unitType = UnitType.UNIT;
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
                customerUuid
        );

        when(repository.findAndAggregateAssetsByCustomerId(customerId)).thenReturn(Optional.of(List.of(assetGroup)));
        when(financeClient.findFinanceByTypeAndUri(assetType, uri))
                .thenReturn(new FinanceResponse(
                        name,
                        symbol,
                        uri,
                        unitType,
                        "35000",
                        currencyType,
                        assetType,
                        LocalDateTime.now()));

        List<AssetAggregateDTO> assets = assetService.findAllCustomerAssets(customerId, currencyType);

        assertThat(assets).hasSize(1);
        AssetAggregateDTO asset = assets.getFirst();
        assertThat(asset.name()).isEqualTo("Ethereum");
        assertEquals(String.format("%.2f", BigDecimal.valueOf(35000)), String.format("%.2f", asset.currentPrice()));
    }

    @Test
    void findAllCustomerAssets_shouldAggregateAssetsWithFallbackFinance() {
        UUID customerId = UUID.randomUUID();
        String name = "Ethereum";
        String symbol = "ETH";
        String uri = "ethereum";
        AssetType assetType = AssetType.CRYPTO;
        CurrencyType currencyType = CurrencyType.USD;
        UnitType unitType = UnitType.UNIT;
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
                customerUuid
        );

        when(repository.findAndAggregateAssetsByCustomerId(customerId)).thenReturn(Optional.of(List.of(assetGroup)));
        when(financeClient.findFinanceByTypeAndUri(assetType, uri))
                .thenThrow(new FinanceNotFoundException("Finance not found"));

        List<AssetAggregateDTO> assets = assetService.findAllCustomerAssets(customerId, currencyType);

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
        UUID customerId = UUID.randomUUID();
        String name = "Ethereum";
        String symbol = "ETH";
        String uri = "ethereum";
        AssetType assetType = AssetType.CRYPTO;
        CurrencyType currencyType = CurrencyType.USD;
        UnitType unitType = UnitType.UNIT;
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
                customerId
        );

        Request request = Request.create(
                Request.HttpMethod.GET,
                "http://localhost/api/v1/finances/asset-type/uri",
                Collections.emptyMap(),
                null,
                null,
                null
        );

        when(repository.findAndAggregateAssetsByCustomerId(customerId)).thenReturn(Optional.of(List.of(assetGroup)));
        when(financeClient.findFinanceByTypeAndUri(assetType, uri))
                .thenThrow(new FeignException.InternalServerError(
                        "Finance not found", request, null, null));

        assetService.findAllCustomerAssets(customerId, currencyType);

        verify(support, times(1)).sendNotification(any(SupportConfirmation.class));
    }
}