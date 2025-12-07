package pl.muybien.finance;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.muybien.exception.FinanceNotFoundException;
import pl.muybien.finance.dto.FinanceDetailDTO;
import pl.muybien.entity.helper.FinanceDetail;
import pl.muybien.entity.Finance;
import pl.muybien.enumeration.AssetType;
import pl.muybien.enumeration.CurrencyType;
import pl.muybien.enumeration.UnitType;
import pl.muybien.repository.FinanceRepository;
import pl.muybien.response.FinanceResponse;

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
    private FinanceDTOMapper mapper;

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
                new BigDecimal("45000.00"),
                CurrencyType.USD,
                AssetType.CRYPTO,
                LocalDateTime.now(),
                1
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

    @Test
    void fetchFinance_shouldThrowWhenAssetTypeNotFound() {
        when(financeRepository.findFinanceByAssetType(AssetType.STOCK)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> financeService.fetchFinance(AssetType.STOCK, "aapl"))
                .isInstanceOf(FinanceNotFoundException.class)
                .hasMessageContaining("Finance not found for asset type: STOCK");
    }

    @Test
    void fetchFinance_shouldThrowWhenUriNotFound() {
        when(financeRepository.findFinanceByAssetType(AssetType.CRYPTO)).thenReturn(Optional.of(testFinance));

        assertThatThrownBy(() -> financeService.fetchFinance(AssetType.CRYPTO, "ether"))
                .isInstanceOf(FinanceNotFoundException.class)
                .hasMessageContaining("Finance not found for uri: ether");
    }

    @Test
    void fetchFinance_shouldReturnValidResponse() {
        FinanceResponse mappedResponse = new FinanceResponse(
                "Bitcoin",
                "BTC",
                "bitcoin",
                UnitType.UNIT,
                new BigDecimal("45000.00"),
                CurrencyType.USD,
                AssetType.CRYPTO,
                LocalDateTime.now()
        );

        when(financeRepository.findFinanceByAssetType(AssetType.CRYPTO)).thenReturn(Optional.of(testFinance));
        when(mapper.toResponse(testDetail)).thenReturn(mappedResponse);

        FinanceResponse response = financeService.fetchFinance(AssetType.CRYPTO, "BITCOIN");

        assertThat(response.name()).isEqualTo("Bitcoin");
        assertThat(response.uri()).isEqualTo("bitcoin");
        assertThat(response.price()).isEqualTo("45000.00");
    }

    @Test
    void displayAvailableFinance_shouldReturnEmptySetWhenNoDetails() {
        when(financeRepository.findFinanceByAssetType(AssetType.COMMODITY)).thenReturn(Optional.of(new Finance()));

        Set<FinanceDetailDTO> result = financeService.displayAvailableFinance(AssetType.COMMODITY);

        assertThat(result).isEmpty();
    }

    @Test
    void displayAvailableFinance_shouldReturnSortedDetails() {
        when(financeRepository.findFinanceByAssetType(AssetType.CRYPTO)).thenReturn(Optional.of(testFinance));
        when(mapper.toDetailDTO(testDetail)).thenReturn(
                new FinanceDetailDTO("Bitcoin", "BTC", "bitcoin", UnitType.UNIT, new BigDecimal("45000.00"), CurrencyType.USD, AssetType.CRYPTO, LocalDateTime.now())
        );

        Set<FinanceDetailDTO> result = financeService.displayAvailableFinance(AssetType.CRYPTO);

        assertThat(result).hasSize(1);
        assertThat(result.iterator().next().name()).isEqualTo("Bitcoin");
    }
}