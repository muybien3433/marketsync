package pl.muybien.finance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.muybien.finance.updater.FinanceDatabaseUpdater;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

class FinanceDatabaseUpdaterTest {

    @Mock
    private FinanceRepository repository;

    @InjectMocks
    private FinanceDatabaseUpdater updater;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSortAndSaveFinanceToDatabaseWhenFinanceExists() {
        String assetType = "cryptos";
        LinkedHashSet<FinanceDetail> financeDetails = new LinkedHashSet<>();
        financeDetails.add(new FinanceDetail("Bitcoin", "BTC", "bitcoin", null, CurrencyType.USD, AssetType.CRYPTOS));
        financeDetails.add(new FinanceDetail("Ethereum", "ETH", "ethereum", null, CurrencyType.USD, AssetType.CRYPTOS));

        Finance existingFinance = mock(Finance.class);
        when(repository.findFinanceByAssetTypeIgnoreCase(assetType.toLowerCase())).thenReturn(Optional.of(existingFinance));

        updater.sortAndSaveFinanceToDatabase(assetType, financeDetails);

        verify(existingFinance).getFinanceDetails();
        verify(repository).save(existingFinance);
    }

    @Test
    void testSortAndSaveFinanceToDatabaseWhenFinanceDoesNotExist() {
        String assetType = "stocks";
        LinkedHashSet<FinanceDetail> financeDetails = new LinkedHashSet<>();
        financeDetails.add(new FinanceDetail("Apple", "APPLE", "apple", BigDecimal.ONE, CurrencyType.PLN, AssetType.STOCKS));
        financeDetails.add(new FinanceDetail("Tesla", "TESLA", "tesla", BigDecimal.ONE, CurrencyType.PLN, AssetType.STOCKS));

        when(repository.findFinanceByAssetTypeIgnoreCase(assetType.toLowerCase())).thenReturn(Optional.empty());

        updater.sortAndSaveFinanceToDatabase(assetType, financeDetails);

        verify(repository).save(argThat(finance -> {
            assertEquals(finance.getFinanceDetails().stream()
                    .map(FinanceDetail::getName)
                    .sorted()
                    .toList(), List.of("Apple", "Tesla"));
            return finance.getAssetType().equals(assetType.toLowerCase());
        }));
    }

    @Test
    void testSortAndSaveFinanceToDatabaseWithEmptyDetails() {
        String assetType = "cryptos";
        LinkedHashSet<FinanceDetail> financeDetails = new LinkedHashSet<>();

        when(repository.findFinanceByAssetTypeIgnoreCase(assetType.toLowerCase())).thenReturn(Optional.empty());

        updater.sortAndSaveFinanceToDatabase(assetType, financeDetails);

        verify(repository, never()).findFinanceByAssetTypeIgnoreCase(assetType.toLowerCase());
        verify(repository, never()).save(any());
    }

    @Test
    void testSortAndSaveFinanceToDatabaseWithUnsortedDetails() {
        String assetType = "cryptos";
        LinkedHashSet<FinanceDetail> financeDetails = new LinkedHashSet<>();
        financeDetails.add(new FinanceDetail("Bitcoin", "BTC", "bitcoin", null, CurrencyType.USD, AssetType.CRYPTOS));
        financeDetails.add(new FinanceDetail("Ethereum", "ETH", "ethereum", null, CurrencyType.USD, AssetType.CRYPTOS));

        Finance existingFinance = mock(Finance.class);
        when(repository.findFinanceByAssetTypeIgnoreCase(assetType.toLowerCase())).thenReturn(Optional.of(existingFinance));

        updater.sortAndSaveFinanceToDatabase(assetType, financeDetails);

        verify(existingFinance).getFinanceDetails();
        verify(repository).save(existingFinance);
        verify(existingFinance).getFinanceDetails();
    }

    @Test
    void testSortAndSaveFinanceToDatabaseHandlesCaseInsensitivity() {
        String assetType = "CRYPTOS";
        LinkedHashSet<FinanceDetail> financeDetails = new LinkedHashSet<>();
        financeDetails.add(new FinanceDetail("Bitcoin", "BTC", "bitcoin", null, CurrencyType.USD, AssetType.CRYPTOS));
        financeDetails.add(new FinanceDetail("Ethereum", "ETH", "ethereum", null, CurrencyType.USD, AssetType.CRYPTOS));

        when(repository.findFinanceByAssetTypeIgnoreCase(assetType.toLowerCase())).thenReturn(Optional.empty());

        updater.sortAndSaveFinanceToDatabase(assetType, financeDetails);

        verify(repository).findFinanceByAssetTypeIgnoreCase(assetType.toLowerCase());
        verify(repository).save(any(Finance.class));
    }

    @Test
    void testSortAndSaveFinanceToDatabaseWhenDetailsAreAlreadySorted() {
        String assetType = "cryptos";
        LinkedHashSet<FinanceDetail> financeDetails = new LinkedHashSet<>();
        financeDetails.add(new FinanceDetail("Bitcoin", "BTC", "bitcoin", null, CurrencyType.USD, AssetType.CRYPTOS));
        financeDetails.add(new FinanceDetail("Ethereum", "ETH", "ethereum", null, CurrencyType.USD, AssetType.CRYPTOS));

        Finance existingFinance = mock(Finance.class);
        when(repository.findFinanceByAssetTypeIgnoreCase(assetType.toLowerCase())).thenReturn(Optional.of(existingFinance));

        updater.sortAndSaveFinanceToDatabase(assetType, financeDetails);

        verify(existingFinance).getFinanceDetails();
        verify(repository).save(existingFinance);
    }
}
