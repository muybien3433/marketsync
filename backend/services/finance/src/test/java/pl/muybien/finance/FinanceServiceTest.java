package pl.muybien.finance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.muybien.finance.crypto.CryptoService;
import pl.muybien.finance.currency.CurrencyService;
import pl.muybien.finance.currency.CurrencyType;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FinanceServiceTest {

    @Mock
    private CryptoService cryptoService;

    @Mock
    private CurrencyService currencyService;

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
        String assetType = "cryptos";
        String uri = "bitcoin";
        String currency = "USD";
        var expectedResponse = new FinanceResponse("Bitcoin", "BTC", BigDecimal.valueOf(100000), CurrencyType.USD, assetType);
        when(cryptoService.fetchCrypto(uri, assetType, currency)).thenReturn(expectedResponse);

        FinanceResponse result = financeService.fetchFinance(assetType, uri, currency);

        assertEquals(expectedResponse, result);
        verify(cryptoService).fetchCrypto(uri, assetType, currency);
        verifyNoInteractions(repository, currencyService);
    }

    @Test
    void testFetchFinanceWithUriOnly() {
        String assetType = "cryptos";
        String uri = "ethereum";
        var expectedResponse = new FinanceResponse("Ethereum", "ETH", BigDecimal.valueOf(3000), CurrencyType.USD, assetType);

        when(cryptoService.fetchCrypto(uri, assetType)).thenReturn(expectedResponse);

        FinanceResponse result = financeService.fetchFinance(assetType, uri);

        assertEquals(expectedResponse, result);
        verify(cryptoService).fetchCrypto(uri, assetType);
        verifyNoInteractions(repository, currencyService);
    }

    @Test
    void testFindExchangeRate() {
        String from = "USD";
        String to = "EUR";
        CurrencyType fromCurrency = CurrencyType.fromString(from);
        CurrencyType toCurrency = CurrencyType.fromString(to);
        BigDecimal expectedRate = BigDecimal.valueOf(0.85);

        when(currencyService.getCurrencyPairExchange(fromCurrency, toCurrency)).thenReturn(expectedRate);

        BigDecimal result = financeService.findExchangeRate(from, to);

        assertEquals(expectedRate, result);
        verify(currencyService).getCurrencyPairExchange(fromCurrency, toCurrency);
        verifyNoInteractions(repository, cryptoService);
    }

    @Test
    void testDisplayAvailableFinance() {
        String assetType = "cryptos";
        Set<FinanceDetail> expectedDetails = Set.of(new FinanceDetail("Bitcoin", "BTC", "bitcoin"));
        Finance finance = mock(Finance.class);

        when(repository.findFinanceByAssetType(assetType)).thenReturn(Optional.of(finance));
        when(finance.getFinanceDetails()).thenReturn(expectedDetails);

        Set<FinanceDetail> result = financeService.displayAvailableFinance(assetType);

        assertEquals(expectedDetails, result);
        verify(repository).findFinanceByAssetType(assetType);
        verify(finance).getFinanceDetails();
        verifyNoInteractions(cryptoService, currencyService);
    }

    @Test
    void testDisplayAvailableFinanceWhenEmpty() {
        String assetType = "stocks";

        when(repository.findFinanceByAssetType(assetType)).thenReturn(Optional.empty());

        Set<FinanceDetail> result = financeService.displayAvailableFinance(assetType);

        assertTrue(result.isEmpty());
        verify(repository).findFinanceByAssetType(assetType);
        verifyNoInteractions(cryptoService, currencyService);
    }
}