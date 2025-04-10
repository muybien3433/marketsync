package pl.muybien.finance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.muybien.enums.CurrencyType;
import pl.muybien.finance.currency.CurrencyService;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FinanceServiceTest {

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
    void testFindExchangeRate() {
        CurrencyType from = CurrencyType.USD;
        CurrencyType to = CurrencyType.EUR;
        BigDecimal expectedRate = BigDecimal.valueOf(0.85);

        when(currencyService.getCurrencyPairExchange(from, to)).thenReturn(expectedRate);

        BigDecimal result = financeService.findExchangeRate(from, to);

        assertEquals(expectedRate, result);
        verify(currencyService).getCurrencyPairExchange(from, to);
    }
}
