package pl.muybien.asset;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
        var assets = List.of(
                new AssetDTO(1L, "Bitcoin", AssetType.CRYPTO.toString(), BigDecimal.valueOf(2),
                        BigDecimal.valueOf(30000), "USD", BigDecimal.valueOf(60000),
                        BigDecimal.valueOf(20000), LocalDateTime.now(), BigDecimal.valueOf(10000), BigDecimal.valueOf(50))
        );
        when(assetService.findAllCustomerAssets(authHeader)).thenReturn(assets);

        var response = assetController.findAllCustomerAssets(authHeader);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(assets);
        verify(assetService).findAllCustomerAssets(authHeader);
    }

    @Test
    void findAllHistoryAssets_shouldReturnHistoryList() {
        var history = List.of(new AssetHistoryDTO(1L, "Bitcoin", AssetType.CRYPTO,
                        BigDecimal.valueOf(2), BigDecimal.valueOf(100000), LocalDateTime.now())
        );

        when(assetService.findAllAssetHistory(authHeader)).thenReturn(history);

        var response = assetController.findAllHistoryAssets(authHeader);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(history);
        verify(assetService).findAllAssetHistory(authHeader);
    }

    @Test
    void createAsset_shouldReturnCreatedStatus() {
        AssetRequest request = new AssetRequest(
                AssetType.CRYPTO, "bitcoin", BigDecimal.valueOf(2), BigDecimal.valueOf(30000));

        doNothing().when(assetService).createAsset(authHeader, request);

        var response = assetController.createAsset(authHeader, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        verify(assetService).createAsset(authHeader, request);
    }

    @Test
    void updateAsset_shouldReturnOkStatus() {
        Long assetId = 1L;
        AssetRequest request = new AssetRequest(
                AssetType.CRYPTO, "bitcoin", BigDecimal.valueOf(3), BigDecimal.valueOf(40000));

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
