package pl.muybien.finance;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.muybien.entity.Currency;
import pl.muybien.enumeration.CurrencyType;
import pl.muybien.exception.FinanceNotFoundException;
import pl.muybien.repository.CurrencyRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CurrencyServiceTest {

    @Mock
    private CurrencyRepository currencyRepository;

    @InjectMocks
    private CurrencyService currencyService;

    @Test
    void findExchangeRate_shouldThrowWhenNotFound() {
        when(currencyRepository.findById(CurrencyType.GBP.name())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> currencyService.findExchangeRate(CurrencyType.USD, CurrencyType.GBP))
                .isInstanceOf(FinanceNotFoundException.class)
                .hasMessageContaining("Missing rate USD/GBP");
    }

    @Test
    void findExchangeRate_shouldThrowWhenFromIsNull() {
        assertThatThrownBy(() -> currencyService.findExchangeRate(null, CurrencyType.USD))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Currencies required");
    }

    @Test
    void findExchangeRate_shouldThrowWhenToIsNull() {
        assertThatThrownBy(() -> currencyService.findExchangeRate(CurrencyType.USD, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Currencies required");
    }

    @Test
    void findExchangeRate_shouldReturnOneWhenCurrenciesAreEqual() {
        var result = currencyService.findExchangeRate(CurrencyType.EUR, CurrencyType.EUR);

        assertThat(result).isEqualByComparingTo(BigDecimal.ONE);
        verifyNoInteractions(currencyRepository);
    }

    @Test
    void findExchangeRate_shouldReturnDirectRateWhenFromUsd() {
        Currency pln = mock(Currency.class);
        when(pln.getExchangeFromUSD()).thenReturn(new BigDecimal("4.0"));
        when(currencyRepository.findById(CurrencyType.PLN.name())).thenReturn(Optional.of(pln));

        var result = currencyService.findExchangeRate(CurrencyType.USD, CurrencyType.PLN);

        assertThat(result).isEqualByComparingTo("4.000000000000");
        verify(currencyRepository).findById("PLN");
    }

    @Test
    void findExchangeRate_shouldReturnInverseRateWhenToUsd() {
        Currency pln = mock(Currency.class);
        when(pln.getExchangeFromUSD()).thenReturn(new BigDecimal("4.0"));
        when(currencyRepository.findById(CurrencyType.PLN.name())).thenReturn(Optional.of(pln));

        var result = currencyService.findExchangeRate(CurrencyType.PLN, CurrencyType.USD);

        assertThat(result).isEqualByComparingTo("0.250000000000");
        verify(currencyRepository).findById("PLN");
    }

    @Test
    void findExchangeRate_shouldComputeCrossRateUsingUsdRates() {
        Currency eur = mock(Currency.class);
        Currency gbp = mock(Currency.class);

        when(eur.getExchangeFromUSD()).thenReturn(new BigDecimal("0.8"));
        when(gbp.getExchangeFromUSD()).thenReturn(new BigDecimal("0.5"));

        when(currencyRepository.findById(CurrencyType.EUR.name())).thenReturn(Optional.of(eur));
        when(currencyRepository.findById(CurrencyType.GBP.name())).thenReturn(Optional.of(gbp));

        var result = currencyService.findExchangeRate(CurrencyType.EUR, CurrencyType.GBP);

        assertThat(result).isEqualByComparingTo("0.625000000000");
        verify(currencyRepository).findById("EUR");
        verify(currencyRepository).findById("GBP");
    }

    @Test
    void findExchangeRate_shouldThrowWhenRateNonPositive() {
        Currency pln = mock(Currency.class);
        when(pln.getExchangeFromUSD()).thenReturn(BigDecimal.ZERO);
        when(currencyRepository.findById(CurrencyType.PLN.name())).thenReturn(Optional.of(pln));

        assertThatThrownBy(() -> currencyService.findExchangeRate(CurrencyType.USD, CurrencyType.PLN))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Non-positive rate for USD/PLN");
    }
}