package pl.muybien.finance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.muybien.currency.CurrencyRepository;
import pl.muybien.enums.AssetType;
import pl.muybien.enums.CurrencyType;
import pl.muybien.enums.UnitType;
import pl.muybien.exception.FinanceNotFoundException;
import pl.muybien.currency.Currency;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FinanceServiceTest {

    @Mock
    private FinanceRepository financeRepository;

    @Mock
    private CurrencyRepository currencyRepository;

    @Mock
    private FinanceDetailDTOMapper mapper;

    @InjectMocks
    private FinanceService financeService;

    private Finance testFinance;
    private FinanceDetail testDetail;

    @BeforeEach
    void setUp() {
        testDetail = new FinanceDetail(
                "Bitcoin",
                "BTC",
                "bitcoin",
                UnitType.UNIT.name(),
                "45000.00",
                CurrencyType.USD.name(),
                AssetType.CRYPTO.name(),
                LocalDateTime.now()
        );

        Map<String, Map<String, FinanceDetail>> details = new HashMap<>();
        details.put("crypto", Map.of("bitcoin", testDetail));

        testFinance = new Finance();
        testFinance.setFinanceDetails(details);
    }

    @Test
    void fetchFinance_shouldThrowWhenUriBlank() {
        assertThatThrownBy(() -> financeService.fetchFinance("crypto", "   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Finance identifier cannot be null or blank");
    }

    @Test
    void fetchFinance_shouldThrowWhenAssetTypeNotFound() {
        when(financeRepository.findFinanceByAssetType("stocks")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> financeService.fetchFinance("STOCKS", "aapl"))
                .isInstanceOf(FinanceNotFoundException.class)
                .hasMessageContaining("Finance not found for asset type: stocks");
    }

    @Test
    void fetchFinance_shouldThrowWhenUriNotFound() {
        when(financeRepository.findFinanceByAssetType("crypto")).thenReturn(Optional.of(testFinance));

        assertThatThrownBy(() -> financeService.fetchFinance("CRYPTO", "ether"))
                .isInstanceOf(FinanceNotFoundException.class)
                .hasMessageContaining("Finance not found for uri: ether");
    }

    @Test
    void fetchFinance_shouldReturnValidResponse() {
        when(financeRepository.findFinanceByAssetType("crypto")).thenReturn(Optional.of(testFinance));

        FinanceResponse response = financeService.fetchFinance("CRYPTO", "BITCOIN");

        assertThat(response.name()).isEqualTo("Bitcoin");
        assertThat(response.uri()).isEqualTo("bitcoin");
        assertThat(response.price()).isEqualTo("45000.00");
    }

    @Test
    void displayAvailableFinance_shouldReturnEmptySetWhenNoDetails() {
        when(financeRepository.findFinanceByAssetType("forex")).thenReturn(Optional.of(new Finance()));

        Set<FinanceDetailDTO> result = financeService.displayAvailableFinance("FOREX");

        assertThat(result).isEmpty();
    }

    @Test
    void displayAvailableFinance_shouldReturnSortedDetails() {
        when(financeRepository.findFinanceByAssetType("crypto")).thenReturn(Optional.of(testFinance));
        when(mapper.toDTO(testDetail)).thenReturn(
                new FinanceDetailDTO("Bitcoin", "BTC", "bitcoin", "UNIT", "45000.00", "USD", "CRYPTO", LocalDateTime.now())
        );

        Set<FinanceDetailDTO> result = financeService.displayAvailableFinance("crypto");

        assertThat(result).hasSize(1);
        assertThat(result.iterator().next().name()).isEqualTo("Bitcoin");
    }

    @Test
    void displayAvailableFinanceWithCurrency_shouldConvertPrices() {
        Currency usdToEur = new Currency(
                "usd-eur",
                new BigDecimal("0.85"),
                "RATE",
                LocalDateTime.of(2024, 1, 1, 12, 0)
        );

        when(financeRepository.findFinanceByAssetType("crypto")).thenReturn(Optional.of(testFinance));
        when(currencyRepository.findCurrencyByName("usd-eur")).thenReturn(Optional.of(usdToEur));
        when(mapper.toDTO(testDetail)).thenReturn(
                new FinanceDetailDTO("Bitcoin", "BTC", "bitcoin", "UNIT", "45000.00", "USD", "CRYPTO", LocalDateTime.now())
        );

        Set<FinanceDetailDTO> result = financeService.displayAvailableFinance("crypto", "EUR");

        assertThat(new BigDecimal(result.iterator().next().price())).isEqualByComparingTo("38250.00");
        verify(currencyRepository, times(1)).findCurrencyByName("usd-eur");
    }

    @Test
    void convertCurrencyIfNecessary_shouldUseCache() {
        Map<CurrencyType, BigDecimal> cache = new HashMap<>();
        FinanceDetailDTO original = new FinanceDetailDTO(
                "Gold", "XAU", "gold", "OUNCE", "1800.00", "USD", "COMMODITY", LocalDateTime.now()
        );

        when(currencyRepository.findCurrencyByName("usd-cny"))
                .thenReturn(Optional.of(new Currency(
                                "usd-cny",
                                new BigDecimal("110.00"),
                                "OUNCE",
                                LocalDateTime.of(2024, 1, 1, 12, 0)
                        ))
                );

        FinanceDetailDTO converted1 = financeService.convertCurrencyIfNecessary(
                original, CurrencyType.CNY, cache
        );

        FinanceDetailDTO converted2 = financeService.convertCurrencyIfNecessary(
                original, CurrencyType.CNY, cache
        );

        assertThat(new BigDecimal(converted1.price())).isEqualByComparingTo("198000.00");
        verify(currencyRepository, times(1)).findCurrencyByName("usd-cny");
    }

    @Test
    void findExchangeRate_shouldThrowWhenNotFound() {
        when(currencyRepository.findCurrencyByName("usd-gbp")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> financeService.findExchangeRate(CurrencyType.USD, CurrencyType.GBP))
                .isInstanceOf(FinanceNotFoundException.class)
                .hasMessageContaining("Could not find currency pair for USD to GBP");
    }

    @Test
    void currencyNameResolver_shouldReturnLowercasePair() {
        String result = financeService.currencyNameResolver(CurrencyType.USD, CurrencyType.EUR);
        assertThat(result).isEqualTo("usd-eur");
    }

    @Test
    void displayAvailableFinance_shouldHandleNullCurrencyType() {
        assertThatThrownBy(() -> financeService.displayAvailableFinance("crypto", null))
                .isInstanceOf(FinanceNotFoundException.class);
    }

    @Test
    void convertCurrencyIfNecessary_shouldHandleZeroPrice() {
        FinanceDetailDTO original = new FinanceDetailDTO(
                "Silver", "XAG", "silver", "OUNCE", "0.00", "USD", "COMMODITY", LocalDateTime.now()
        );

        FinanceDetailDTO result = financeService.convertCurrencyIfNecessary(
                original, CurrencyType.EUR, new HashMap<>()
        );

        assertThat(result.price()).isEqualTo("0.00");
    }
}