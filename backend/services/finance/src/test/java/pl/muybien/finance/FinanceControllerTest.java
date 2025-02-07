package pl.muybien.finance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class FinanceControllerTest {

    @Mock
    private FinanceService financeService;

    @InjectMocks
    private FinanceController financeController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(financeController).build();
    }

    @Test
    void testFindFinanceByTypeAndUriAndCurrency() throws Exception {
        String assetType = "cryptos";
        String uri = "bitcoin";
        String currency = "USD";
        var response = new FinanceResponse("Bitcoin", "BTC", BigDecimal.valueOf(100000), CurrencyType.USD, AssetType.CRYPTOS);

        when(financeService.fetchFinance(assetType, uri)).thenReturn(response);

        mockMvc.perform(get("/api/v1/finances/{asset-type}/{uri}", assetType, uri, currency))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isNotEmpty());

        verify(financeService).fetchFinance(assetType, uri);
    }

    @Test
    void testFindFinanceWithDefaultCurrency() throws Exception {
        String assetType = "cryptos";
        String uri = "ethereum";
        var response = new FinanceResponse("Ethereum", "BTC", BigDecimal.valueOf(3000), CurrencyType.USD, AssetType.CRYPTOS);

        when(financeService.fetchFinance(assetType, uri)).thenReturn(response);

        mockMvc.perform(get("/api/v1/finances/{asset-type}/{uri}", assetType, uri))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isNotEmpty());

        verify(financeService).fetchFinance(assetType, uri);
    }

    @Test
    void testDisplayAvailableFinance() throws Exception {
        String assetType = "cryptos";
        Set<FinanceDetail> details = Set.of(
                new FinanceDetail("Bitcoin", "BTC", "bitcoin", null, CurrencyType.USD, AssetType.CRYPTOS),
                new FinanceDetail("Ethereum", "ETH", "Ethereum", null, CurrencyType.USD, AssetType.CRYPTOS));

        when(financeService.displayAvailableFinance(assetType)).thenReturn(details);

        mockMvc.perform(get("/api/v1/finances/{asset-type}", assetType))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(details.size()));

        verify(financeService).displayAvailableFinance(assetType);
    }

    @Test
    void testFindExchangeRate() throws Exception {
        CurrencyType from = CurrencyType.USD;
        CurrencyType to = CurrencyType.EUR;
        BigDecimal exchangeRate = BigDecimal.valueOf(1.2);

        when(financeService.findExchangeRate(from, to)).thenReturn(exchangeRate);

        mockMvc.perform(get("/api/v1/finances/currencies/{from}/{to}", from, to))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(exchangeRate.doubleValue()));

        verify(financeService).findExchangeRate(from, to);
    }
}
