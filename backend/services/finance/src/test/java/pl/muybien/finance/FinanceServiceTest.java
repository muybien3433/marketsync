package pl.muybien.finance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.muybien.finance.crypto.CryptoService;
import pl.muybien.finance.currency.CurrencyService;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FinanceServiceTest {

    @Mock
    private CryptoService cryptoService;

    @Mock
    private CurrencyService currencyService;

    @Mock
    private FinanceDetailDTOMapper mapper;

    @Mock
    private FinanceRepository repository;

    @InjectMocks
    private FinanceService financeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFetchFinanceWithUriAndCurrency() {
        AssetType assetType = AssetType.CRYPTOS;
        String uri = "bitcoin";
        CurrencyType currency = CurrencyType.USD;
        var expectedResponse = new FinanceResponse("Bitcoin", "BTC", BigDecimal.valueOf(100000), currency, assetType);
        when(cryptoService.fetchCrypto(uri, assetType)).thenReturn(expectedResponse);

        FinanceResponse result = financeService.fetchFinance(assetType.name(), uri);

        assertEquals(expectedResponse, result);
        verify(cryptoService).fetchCrypto(uri, assetType);
        verifyNoInteractions(repository, currencyService);
    }

    @Test
    void testFetchFinanceWithUriOnly() {
        AssetType assetType = AssetType.CRYPTOS;
        String uri = "ethereum";
        var expectedResponse = new FinanceResponse("Ethereum", "ETH", BigDecimal.valueOf(3000), CurrencyType.USD, assetType);

        when(cryptoService.fetchCrypto(uri, assetType)).thenReturn(expectedResponse);

        FinanceResponse result = financeService.fetchFinance(assetType.name(), uri);

        assertEquals(expectedResponse, result);
        verify(cryptoService).fetchCrypto(uri, assetType);
        verifyNoInteractions(repository, currencyService);
    }

    @Test
    void testFindExchangeRate() {
        CurrencyType from = CurrencyType.USD;
        CurrencyType to = CurrencyType.EUR;
        BigDecimal expectedRate = BigDecimal.valueOf(0.85);

        when(currencyService.getCurrencyPairExchange(from, to)).thenReturn(expectedRate);

        BigDecimal result = financeService.findExchangeRate(from, to);

        assertEquals(expectedRate, result);
        verify(currencyService).getCurrencyPairExchange(from, to);
        verifyNoInteractions(repository, cryptoService);
    }

    @Test
    void testDisplayAvailableFinance() {
        String assetType = "cryptos";
        Set<FinanceDetail> expectedDetails = Set.of(new FinanceDetail("Bitcoin", "BTC", "bitcoin", null, CurrencyType.USD, AssetType.CRYPTOS));
        Finance finance = mock(Finance.class);

        when(repository.findFinanceByAssetTypeIgnoreCase(assetType)).thenReturn(Optional.of(finance));
        when(finance.getFinanceDetails()).thenReturn(expectedDetails);

        Set<FinanceDetailDTO> result = financeService.displayAvailableFinance(assetType);

        assertEquals(expectedDetails.size(), result.size());
        verify(repository).findFinanceByAssetTypeIgnoreCase(assetType);
        verify(finance).getFinanceDetails();
        verifyNoInteractions(cryptoService, currencyService);
    }

    @Test
    void testDisplayAvailableFinanceWhenEmpty() {
        String assetType = "stocks";

        when(repository.findFinanceByAssetTypeIgnoreCase(assetType)).thenReturn(Optional.empty());

        Set<FinanceDetailDTO> result = financeService.displayAvailableFinance(assetType);

        assertTrue(result.isEmpty());
        verify(repository).findFinanceByAssetTypeIgnoreCase(assetType);
        verifyNoInteractions(cryptoService, currencyService);
    }
}