package pl.muybien.asset;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.muybien.asset.dto.AssetAggregateDTO;
import pl.muybien.asset.dto.AssetHistoryDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AssetControllerTest {

    @Mock
    private AssetService assetService;

    @InjectMocks
    private AssetController assetController;

    private final String authHeader = "Bearer token";

    AssetControllerTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAllCustomerAssets_shouldReturnAssetList() {
        String desiredCurrency = "USD";
        BigDecimal exchangeRateToDesired = new BigDecimal("0.01");
        var assets = List.of(
                new AssetAggregateDTO("Bitcoin", "BTC", AssetType.CRYPTOS.name(), BigDecimal.valueOf(2),
                        BigDecimal.valueOf(30000), "USD", BigDecimal.valueOf(60000),
                        BigDecimal.valueOf(20000), BigDecimal.valueOf(10000), BigDecimal.valueOf(50), exchangeRateToDesired)
        );
        when(assetService.findAllCustomerAssets(authHeader, desiredCurrency)).thenReturn(assets);

        var response = assetController.findAllCustomerAssets(authHeader, desiredCurrency);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(assets);
        verify(assetService).findAllCustomerAssets(authHeader, desiredCurrency);
    }

    @Test
    void findAllHistoryAssets_shouldReturnHistoryList() {
        var history = List.of(new AssetHistoryDTO(1L, "Bitcoin", "BTC",
                        BigDecimal.valueOf(2), "USD", BigDecimal.valueOf(100000), LocalDateTime.now(), AssetType.CRYPTOS)
        );

        when(assetService.findAllAssetHistory(authHeader)).thenReturn(history);

        var response = assetController.findAllHistoryAssets(authHeader);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(history);
        verify(assetService).findAllAssetHistory(authHeader);
    }

    @Test
    void createAsset_shouldReturnCreatedStatus() {
        String currency = "USD";
        AssetRequest request = new AssetRequest(
                "cryptos", "bitcoin", BigDecimal.valueOf(2), BigDecimal.valueOf(30000), currency);

        doNothing().when(assetService).createAsset(authHeader, request);

        var response = assetController.createAsset(authHeader, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        verify(assetService).createAsset(authHeader, request);
    }

    @Test
    void updateAsset_shouldReturnOkStatus() {
        Long assetId = 1L;
        String currency = "USD";
        AssetRequest request = new AssetRequest(
                "cryptos", "bitcoin", BigDecimal.valueOf(3), BigDecimal.valueOf(40000), currency);

        doNothing().when(assetService).updateAsset(authHeader, request, assetId);

        ResponseEntity<String> response = assetController.updateAsset(authHeader, request, assetId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(assetService).updateAsset(authHeader, request, assetId);
    }

    @Test
    void deleteAsset_shouldReturnNoContentStatus() {
        Long assetId = 1L;

        doNothing().when(assetService).deleteAsset(authHeader, assetId);

        var response = assetController.deleteAsset(authHeader, assetId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(assetService).deleteAsset(authHeader, assetId);
    }
}
