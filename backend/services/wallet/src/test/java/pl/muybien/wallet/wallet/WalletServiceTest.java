package pl.muybien.wallet.wallet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.muybien.wallet.asset.Asset;
import pl.muybien.wallet.asset.AssetDTO;
import pl.muybien.wallet.customer.CustomerClient;
import pl.muybien.wallet.customer.CustomerResponse;
import pl.muybien.wallet.exception.*;
import pl.muybien.wallet.finance.FinanceClient;
import pl.muybien.wallet.finance.FinanceResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.Mockito.*;

class WalletServiceTest {

    @InjectMocks
    private WalletService service;

    @Mock
    private CustomerClient customerClient;

    @Mock
    private FinanceClient financeClient;

    @Mock
    private WalletRepository walletRepository;

    private static final String authHeader = "Bearer token";
    private static final String customerId = "test123";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void displayOrCreateWallet_shouldThrowCustomerNotFoundExceptionWhenCustomerDoesNotExist() {
        when(customerClient.fetchCustomerFromHeader(authHeader)).thenReturn(null);

        assertThatThrownBy(() -> service.displayOrCreateWallet(authHeader))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessageContaining("Customer not found");
    }

    @Test
    void findCustomerWallet_shouldReturnWalletWhenFound() {
        var customer = CustomerResponse.builder()
                .id(customerId)
                .build();
        var wallet = Wallet.builder()
                .customerId(customer.id())
                .assets(Collections.emptyList())
                .build();

        when(customerClient.fetchCustomerFromHeader(authHeader)).thenReturn(customer);
        when(walletRepository.findByCustomerId(customer.id())).thenReturn(Optional.of(wallet));

        Wallet result = service.findCustomerWallet(authHeader);

        assertThat(result).isEqualTo(wallet);
        verify(walletRepository).findByCustomerId(customerId);
    }

    @Test
    void findCustomerWallet_shouldThrowExceptionWhenNotFound() {
        var customer = CustomerResponse.builder()
                .id(customerId)
                .build();
        when(customerClient.fetchCustomerFromHeader(authHeader)).thenReturn(customer);
        when(walletRepository.findByCustomerId(customerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findCustomerWallet(authHeader))
                .isInstanceOf(WalletNotFoundException.class)
                .hasMessage("Wallet not found");

        verify(walletRepository).findByCustomerId(customerId);
    }

    @Test
    void findAndAggregateAllWalletAssets_shouldReturnEmptyListWhenNoAssets() {
        var wallet = Wallet.builder()
                .customerId(customerId)
                .assets(null)
                .build();

        List<AssetDTO> result = service.findAndAggregateAllWalletAssets(wallet, customerId);

        assertThat(result).isEmpty();
    }

    @Test
    void findAndAggregateAllWalletAssets_shouldThrowOwnershipExceptionForCustomerIdMismatch() {
        String otherCustomerId = "wrong123";
        var wallet = Wallet.builder()
                .customerId(otherCustomerId)
                .assets(List.of(Asset.builder().customerId(customerId).build()))
                .build();

        Throwable thrown = catchThrowable(() -> service.findAndAggregateAllWalletAssets(wallet, customerId));
        assertThat(thrown)
                .isInstanceOf(OwnershipException.class)
                .hasMessage("Wallet displaying failed:: Customer id mismatch");
    }

    @Test
    void findAndAggregateAllWalletAssets_shouldAggregateAndMapAssets() {
        var asset1 = Asset.builder()
                .name("Bitcoin")
                .count(BigDecimal.valueOf(0.5))
                .purchasePrice(BigDecimal.valueOf(20000))
                .createdDate(LocalDateTime.of(2022, 1, 1, 1, 1))
                .build();
        var asset2 = Asset.builder()
                .name("Bitcoin")
                .count(BigDecimal.valueOf(0.3))
                .purchasePrice(BigDecimal.valueOf(21000))
                .createdDate(LocalDateTime.of(2023, 1, 1, 1, 1))
                .build();
        var asset3 = Asset.builder()
                .name("Ethereum")
                .count(BigDecimal.valueOf(2.0))
                .purchasePrice(BigDecimal.valueOf(1500))
                .createdDate(LocalDateTime.of(2022, 5, 1, 1, 1))
                .build();
        var wallet = Wallet.builder()
                .customerId(customerId)
                .createdDate(LocalDateTime.now())
                .assets(List.of(asset1, asset2, asset3))
                .build();
        var bitcoinResponse = FinanceResponse.builder()
                .name("Bitcoin")
                .priceUsd(BigDecimal.valueOf(45_000))
                .build();
        var ethereumResponse = FinanceResponse.builder()
                .name("Ethereum")
                .priceUsd(BigDecimal.valueOf(3000))
                .build();

        when(financeClient.findFinanceByUri("Bitcoin")).thenReturn(bitcoinResponse);
        when(financeClient.findFinanceByUri("Ethereum")).thenReturn(ethereumResponse);
        List<AssetDTO> result = service.findAndAggregateAllWalletAssets(wallet, customerId);

        assertThat(result).hasSize(2);

        AssetDTO btc = result.stream().filter(dto -> dto.name().equals("Bitcoin")).findFirst().orElseThrow();
        assertThat(btc.count()).isEqualByComparingTo(BigDecimal.valueOf(0.8));
        assertThat(btc.averagePurchasePrice()).isEqualByComparingTo(BigDecimal.valueOf(20500));
        assertThat(btc.currentPrice()).isEqualByComparingTo(BigDecimal.valueOf(45000));
        assertThat(btc.investmentStartDate()).isEqualTo(LocalDate.of(2022, 1, 1));
        assertThat(btc.value()).isEqualByComparingTo(BigDecimal.valueOf(36000));
        assertThat(btc.profit()).isEqualByComparingTo(BigDecimal.valueOf(19600));
        assertThat(btc.profitInPercentage()).isEqualByComparingTo(BigDecimal.valueOf(119.51));

        AssetDTO eth = result.stream().filter(dto -> dto.name().equals("Ethereum")).findFirst().orElseThrow();
        assertThat(eth.count()).isEqualByComparingTo(BigDecimal.valueOf(2.0));
        assertThat(eth.averagePurchasePrice()).isEqualByComparingTo(BigDecimal.valueOf(1500));
        assertThat(eth.currentPrice()).isEqualByComparingTo(BigDecimal.valueOf(3000));
        assertThat(eth.investmentStartDate()).isEqualTo(LocalDate.of(2022, 5, 1));
        assertThat(eth.value()).isEqualByComparingTo(BigDecimal.valueOf(6000));
        assertThat(eth.profit()).isEqualByComparingTo(BigDecimal.valueOf(3000));
        assertThat(eth.profitInPercentage()).isEqualByComparingTo(BigDecimal.valueOf(100.0));

        verify(financeClient, times(1)).findFinanceByUri("Bitcoin");
        verify(financeClient, times(1)).findFinanceByUri("Ethereum");
    }

    @Test
    void fetchFinance_shouldReturnFinanceResponseWhenFinanceExists() {
        String uri = "Bitcoin";
        FinanceResponse expectedFinance = FinanceResponse.builder()
                .name("Bitcoin")
                .priceUsd(BigDecimal.valueOf(50000))
                .build();

        when(financeClient.findFinanceByUri(uri)).thenReturn(expectedFinance);

        FinanceResponse result = service.fetchFinance(uri);

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("Bitcoin");
        assertThat(result.priceUsd()).isEqualByComparingTo(BigDecimal.valueOf(50000));
        verify(financeClient).findFinanceByUri(uri);
    }

    @Test
    void fetchFinance_shouldThrowExceptionWhenFinanceNotFound() {
        String notExistingUri = "NonExistentFinance";
        when(financeClient.findFinanceByUri(notExistingUri)).thenReturn(null);

        assertThatThrownBy(() -> service.fetchFinance(notExistingUri))
                .isInstanceOf(FinanceNotFoundException.class)
                .hasMessage("Finance not found for URI: NonExistentFinance");
        verify(financeClient).findFinanceByUri(notExistingUri);
    }
}