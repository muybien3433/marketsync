package pl.muybien.finance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class FinanceUpdaterTest {

    @Mock
    private FinanceRepository repository;

    @InjectMocks
    private FinanceUpdater financeUpdater;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSortAndSaveFinanceToDatabaseWhenFinanceExists() {
        String assetType = "cryptos";
        LinkedHashSet<FinanceDetail> financeDetails = new LinkedHashSet<>();
        financeDetails.add(new FinanceDetail("Bitcoin", "BTC", "bitcoin"));
        financeDetails.add(new FinanceDetail("Ethereum", "ETH", "ethereum"));

        Finance existingFinance = mock(Finance.class);
        when(repository.findFinanceByAssetType(assetType.toLowerCase())).thenReturn(Optional.of(existingFinance));

        financeUpdater.sortAndSaveFinanceToDatabase(assetType, financeDetails);

        verify(existingFinance).getFinanceDetails();
        verify(repository).save(existingFinance);
    }

    @Test
    void testSortAndSaveFinanceToDatabaseWhenFinanceDoesNotExist() {
        String assetType = "stocks";
        LinkedHashSet<FinanceDetail> financeDetails = new LinkedHashSet<>();
        financeDetails.add(new FinanceDetail("Apple", "APPLE", "apple"));
        financeDetails.add(new FinanceDetail("Tesla", "TESLA", "tesla"));

        when(repository.findFinanceByAssetType(assetType.toLowerCase())).thenReturn(Optional.empty());

        financeUpdater.sortAndSaveFinanceToDatabase(assetType, financeDetails);

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

        when(repository.findFinanceByAssetType(assetType.toLowerCase())).thenReturn(Optional.empty());

        financeUpdater.sortAndSaveFinanceToDatabase(assetType, financeDetails);

        verify(repository, never()).findFinanceByAssetType(assetType.toLowerCase());
        verify(repository, never()).save(any());
    }

    @Test
    void testSortAndSaveFinanceToDatabaseWithUnsortedDetails() {
        String assetType = "cryptos";
        LinkedHashSet<FinanceDetail> financeDetails = new LinkedHashSet<>();
        financeDetails.add(new FinanceDetail("Bitcoin", "BTC", "bitcoin"));
        financeDetails.add(new FinanceDetail("Ethereum", "ETH", "ethereum"));

        Finance existingFinance = mock(Finance.class);
        when(repository.findFinanceByAssetType(assetType.toLowerCase())).thenReturn(Optional.of(existingFinance));

        financeUpdater.sortAndSaveFinanceToDatabase(assetType, financeDetails);

        verify(existingFinance).getFinanceDetails();
        verify(repository).save(existingFinance);
        verify(existingFinance).getFinanceDetails();
    }

    @Test
    void testSortAndSaveFinanceToDatabaseHandlesCaseInsensitivity() {
        String assetType = "CRYPTOS";
        LinkedHashSet<FinanceDetail> financeDetails = new LinkedHashSet<>();
        financeDetails.add(new FinanceDetail("Ethereum", "ETH", "ethereum"));
        financeDetails.add(new FinanceDetail("Bitcoin", "BTC", "bitcoin"));

        when(repository.findFinanceByAssetType(assetType.toLowerCase())).thenReturn(Optional.empty());

        financeUpdater.sortAndSaveFinanceToDatabase(assetType, financeDetails);

        verify(repository).findFinanceByAssetType(assetType.toLowerCase());
        verify(repository).save(any(Finance.class));
    }

    @Test
    void testSortAndSaveFinanceToDatabaseWhenDetailsAreAlreadySorted() {
        String assetType = "cryptos";
        LinkedHashSet<FinanceDetail> financeDetails = new LinkedHashSet<>();
        financeDetails.add(new FinanceDetail("Bitcoin", "BTC", "bitcoin"));
        financeDetails.add(new FinanceDetail("Ethereum", "ETH", "ethereum"));

        Finance existingFinance = mock(Finance.class);
        when(repository.findFinanceByAssetType(assetType.toLowerCase())).thenReturn(Optional.of(existingFinance));

        financeUpdater.sortAndSaveFinanceToDatabase(assetType, financeDetails);

        verify(existingFinance).getFinanceDetails();
        verify(repository).save(existingFinance);
    }
}
