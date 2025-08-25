package pl.muybien.updater;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.muybien.enums.AssetType;
import pl.muybien.finance.Finance;
import pl.muybien.finance.FinanceDetail;
import pl.muybien.finance.FinanceRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DatabaseUpdaterTest {

    @Mock
    private FinanceRepository repository;

    @InjectMocks
    private DatabaseUpdater databaseUpdater;

    @Test
    void saveFinanceToDatabase_shouldUpdateExistingFinance() {
        AssetType assetType = AssetType.CRYPTO;
        Finance existingFinance = new Finance();
        existingFinance.initializeNestedMapIfNeeded(assetType);
        existingFinance.getFinanceDetails().get(assetType).put("btc", mock(FinanceDetail.class));

        when(repository.findFinanceByAssetType(assetType)).thenReturn(Optional.of(existingFinance));

        Map<String, FinanceDetail> newDetails = Map.of(
                "eth", mock(FinanceDetail.class),
                "xrp", mock(FinanceDetail.class)
        );

        databaseUpdater.saveFinanceToDatabase(assetType, newDetails);

        verify(repository).save(existingFinance);
        assertThat(existingFinance.getFinanceDetails().get(assetType))
                .containsAllEntriesOf(newDetails)
                .containsKey("btc");
    }

    @Test
    void saveFinanceToDatabase_shouldCreateNewFinanceWhenNoneExists() {
        AssetType assetType = AssetType.STOCK;
        when(repository.findFinanceByAssetType(assetType)).thenReturn(Optional.empty());

        Map<String, FinanceDetail> newDetails = new HashMap<>();
        newDetails.put("aapl", mock(FinanceDetail.class));
        newDetails.put("googl", mock(FinanceDetail.class));

        databaseUpdater.saveFinanceToDatabase(assetType, newDetails);

        ArgumentCaptor<Finance> captor = ArgumentCaptor.forClass(Finance.class);
        verify(repository).save(captor.capture());

        Finance savedFinance = captor.getValue();
        assertThat(savedFinance.getFinanceDetails().get(assetType).entrySet())
                .containsExactlyInAnyOrderElementsOf(newDetails.entrySet());
    }

//    @Test
//    void saveFinanceToDatabase_shouldNormalizeAssetTypeToLowerCase() {
//        String assetType = "FOREX";
//        String normalizedType = "forex";
//        when(repository.findFinanceByAssetType(normalizedType)).thenReturn(Optional.empty());
//
//        databaseUpdater.saveFinanceToDatabase(assetType, Map.of());
//
//        ArgumentCaptor<Finance> captor = ArgumentCaptor.forClass(Finance.class);
//        verify(repository).save(captor.capture());
//
//        assertThat(captor.getValue().getFinanceDetails())
//                .containsKey(normalizedType);
//    }

    @Test
    void saveFinanceToDatabase_shouldHandleEmptyDetails() {
        AssetType assetType = AssetType.COMMODITY;
        when(repository.findFinanceByAssetType(assetType)).thenReturn(Optional.empty());

        databaseUpdater.saveFinanceToDatabase(assetType, Map.of());

        ArgumentCaptor<Finance> captor = ArgumentCaptor.forClass(Finance.class);
        verify(repository).save(captor.capture());

        assertThat(captor.getValue().getFinanceDetails().get(assetType))
                .isEmpty();
    }

    @Test
    void saveFinanceToDatabase_shouldPreserveExistingAssetTypes() {
        AssetType assetType = AssetType.CRYPTO;
        AssetType otherAssetType = AssetType.STOCK;

        Finance existingFinance = new Finance();
        existingFinance.initializeNestedMapIfNeeded(otherAssetType);
        existingFinance.getFinanceDetails().get(otherAssetType).put("aapl", mock(FinanceDetail.class));

        when(repository.findFinanceByAssetType(assetType)).thenReturn(Optional.of(existingFinance));

        databaseUpdater.saveFinanceToDatabase(assetType, Map.of("btc", mock(FinanceDetail.class)));

        assertThat(existingFinance.getFinanceDetails())
                .containsKeys(assetType, otherAssetType);
    }

    @Test
    void saveFinanceToDatabase_shouldOverwriteExistingEntries() {
        AssetType assetType = AssetType.CRYPTO;
        Finance existingFinance = new Finance();
        existingFinance.initializeNestedMapIfNeeded(assetType);
        FinanceDetail oldDetail = mock(FinanceDetail.class);
        existingFinance.getFinanceDetails().get(assetType).put("btc", oldDetail);

        when(repository.findFinanceByAssetType(assetType)).thenReturn(Optional.of(existingFinance));

        FinanceDetail newDetail = mock(FinanceDetail.class);
        Map<String, FinanceDetail> newDetails = Map.of("btc", newDetail);

        databaseUpdater.saveFinanceToDatabase(assetType, newDetails);

        assertThat(existingFinance.getFinanceDetails().get(assetType).get("btc"))
                .isSameAs(newDetail);
    }
}