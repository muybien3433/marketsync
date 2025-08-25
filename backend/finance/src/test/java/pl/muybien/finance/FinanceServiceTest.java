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
                UnitType.UNIT,
                "45000.00",
                CurrencyType.USD,
                AssetType.CRYPTO,
                LocalDateTime.now()
        );

        Map<AssetType, Map<String, FinanceDetail>> details = new HashMap<>();
        details.put(AssetType.CRYPTO, Map.of("bitcoin", testDetail));

        testFinance = new Finance();
        testFinance.setFinanceDetails(details);
    }

    @Test
    void fetchFinance_shouldThrowWhenUriBlank() {
        assertThatThrownBy(() -> financeService.fetchFinance(AssetType.CRYPTO, "   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Finance identifier cannot be null or blank");
    }

//    @Test
//    void fetchFinance_shouldThrowWhenAssetTypeNotFound() {
//        when(financeRepository.findFinanceByAssetType(AssetType.STOCK)).thenReturn(Optional.empty());
//
//        assertThatThrownBy(() -> financeService.fetchFinance("STOCKS", "aapl"))
//                .isInstanceOf(FinanceNotFoundException.class)
//                .hasMessageContaining("Finance not found for asset type: stocks");
//    }

//    @Test
//    void fetchFinance_shouldThrowWhenUriNotFound() {
//        when(financeRepository.findFinanceByAssetType(AssetType.CRYPTO)).thenReturn(Optional.of(testFinance));
//
//        assertThatThrownBy(() -> financeService.fetchFinance(AssetType.CRYPTO, "ether"))
//                .isInstanceOf(FinanceNotFoundException.class)
//                .hasMessageContaining("Finance not found for uri: ether");
//    }

//    @Test
//    void fetchFinance_shouldReturnValidResponse() {
//        when(financeRepository.findFinanceByAssetType(AssetType.CRYPTO)).thenReturn(Optional.of(testFinance));
//
//        FinanceResponse response = financeService.fetchFinance(AssetType.CRYPTO, "BITCOIN");
//
//        assertThat(response.name()).isEqualTo("Bitcoin");
//        assertThat(response.uri()).isEqualTo("bitcoin");
//        assertThat(response.price()).isEqualTo("45000.00");
//    }

    @Test
    void displayAvailableFinance_shouldReturnEmptySetWhenNoDetails() {
        when(financeRepository.findFinanceByAssetType(AssetType.COMMODITY)).thenReturn(Optional.of(new Finance()));

        Set<FinanceDetailDTO> result = financeService.displayAvailableFinance(AssetType.COMMODITY);

        assertThat(result).isEmpty();
    }

    @Test
    void displayAvailableFinance_shouldReturnSortedDetails() {
        when(financeRepository.findFinanceByAssetType(AssetType.CRYPTO)).thenReturn(Optional.of(testFinance));
        when(mapper.toDTO(testDetail)).thenReturn(
                new FinanceDetailDTO("Bitcoin", "BTC", "bitcoin", UnitType.UNIT, "45000.00", CurrencyType.USD, AssetType.CRYPTO, LocalDateTime.now())
        );

        Set<FinanceDetailDTO> result = financeService.displayAvailableFinance(AssetType.CRYPTO);

        assertThat(result).hasSize(1);
        assertThat(result.iterator().next().name()).isEqualTo("Bitcoin");
    }

//    @Test
//    void displayAvailableFinanceWithCurrency_shouldConvertPrices() {
//        Currency usdToEur = new Currency(
//                "usd-eur",
//                new BigDecimal("0.85"),
//                UnitType.UNIT,
//                LocalDateTime.of(2024, 1, 1, 12, 0)
//        );
//
//        when(financeRepository.findFinanceByAssetType("crypto")).thenReturn(Optional.of(testFinance));
//        when(currencyRepository.findCurrencyByName("usd-eur")).thenReturn(Optional.of(usdToEur));
//        when(mapper.toDTO(testDetail)).thenReturn(
//                new FinanceDetailDTO("Bitcoin", "BTC", "bitcoin", UnitType.UNIT, "45000.00", CurrencyType.USD, AssetType.CRYPTO, LocalDateTime.now())
//        );
//
//        Set<FinanceDetailDTO> result = financeService.displayAvailableFinance("crypto", "EUR");
//
//        assertThat(new BigDecimal(result.iterator().next().price())).isEqualByComparingTo("38250.00");
//        verify(currencyRepository, times(1)).findCurrencyByName("usd-eur");
//    }

//    @Test
//    void convertCurrencyIfNecessary_shouldUseCache() {
//        Map<CurrencyType, BigDecimal> cache = new HashMap<>();
//        FinanceDetailDTO original = new FinanceDetailDTO(
//                "Gold", "XAU", "gold", UnitType.OZ, "1800.00", CurrencyType.USD, AssetType.COMMODITY, LocalDateTime.now()
//        );
//
//        when(currencyRepository.findCurrencyByName("usd-cny"))
//                .thenReturn(Optional.of(new Currency(
//                                "usd-cny",
//                                new BigDecimal("110.00"),
//                                UnitType.OZ,
//                                LocalDateTime.of(2024, 1, 1, 12, 0)
//                        ))
//                );
//
//        FinanceDetailDTO converted1 = financeService.convertCurrencyIfNecessary(
//                original, CurrencyType.CNY, cache
//        );
//
//        assertThat(new BigDecimal(converted1.price())).isEqualByComparingTo("198000.00");
//        verify(currencyRepository, times(1)).findCurrencyByName("usd-cny");
//    }

//    @Test
//    void findExchangeRate_shouldThrowWhenNotFound() {
//        when(currencyRepository.findCurrencyByName("USD/GBP")).thenReturn(Optional.empty());
//
//        assertThatThrownBy(() -> financeService.findExchangeRate(CurrencyType.USD, CurrencyType.GBP))
//                .isInstanceOf(FinanceNotFoundException.class)
//                .hasMessageContaining("Could not find currency pair for USD to GBP");
//    }

    @Test
    void displayAvailableFinance_shouldHandleNullCurrencyType() {
        assertThatThrownBy(() -> financeService.displayAvailableFinance(AssetType.CRYPTO, null))
                .isInstanceOf(FinanceNotFoundException.class);
    }

    @Test
    void convertCurrencyIfNecessary_shouldHandleZeroPrice() {
        FinanceDetailDTO original = new FinanceDetailDTO(
                "Silver", "XAG", "silver", UnitType.OZ, "0.00", CurrencyType.USD, AssetType.COMMODITY, LocalDateTime.now()
        );

        FinanceDetailDTO result = financeService.convertCurrencyIfNecessary(
                original, CurrencyType.EUR, new HashMap<>()
        );

        assertThat(result.price()).isEqualTo("0.00");
    }
}