package pl.muybien.wallet.wallet;

import jakarta.persistence.EntityNotFoundException;
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
    private static final Long customerId = 1L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void displayOrCreateWallet_shouldThrowCustomerNotFoundExceptionWhenCustomerDoesNotExist() {
        when(customerClient.findCustomerById(authHeader, customerId)).thenReturn(Optional.empty());

        WalletRequest request = WalletRequest.builder()
                .authorizationHeader(authHeader)
                .customerId(customerId)
                .build();

        assertThatThrownBy(() -> service.displayOrCreateWallet(authHeader, request))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessageContaining("Wallet not shown:: No Customer exists with ID: %d".formatted(customerId));
    }

    @Test
    void findOrCreateWallet_shouldFindExistingWallet() {
        Wallet existingWallet = Wallet.builder()
                .customerId(customerId)
                .createdDate(LocalDateTime.now())
                .build();
        when(walletRepository.findByCustomerId(customerId)).thenReturn(Optional.of(existingWallet));

        Wallet wallet = service.findOrCreateWallet(customerId);

        assertThat(wallet).isEqualTo(existingWallet);
        verify(walletRepository, times(1)).findByCustomerId(customerId);
        verify(walletRepository, never()).save(any(Wallet.class));
    }

    @Test
    void findOrCreateWallet_shouldCreateNewWallet() {
        when(walletRepository.findByCustomerId(customerId)).thenThrow(EntityNotFoundException.class);

        Wallet newWallet = Wallet.builder()
                .customerId(customerId)
                .createdDate(LocalDateTime.now())
                .build();
        when(walletRepository.save(any(Wallet.class))).thenReturn(newWallet);

        Wallet wallet = service.findOrCreateWallet(customerId);

        assertThat(wallet.getCustomerId()).isEqualTo(customerId);
        assertThat(wallet.getCreatedDate()).isNotNull();
        verify(walletRepository, times(1)).findByCustomerId(customerId);
        verify(walletRepository, times(1)).save(any(Wallet.class));
    }

    @Test
    void findOrCreateWallet_shouldThrowWalletCreationExceptionOnError() {
        when(walletRepository.findByCustomerId(customerId)).thenThrow(new RuntimeException("Database error"));

        assertThatThrownBy(() -> service.findOrCreateWallet(customerId))
                .isInstanceOf(WalletCreationException.class)
                .hasMessageContaining("Wallet could not be created");
    }

    @Test
    void findWalletByCustomerId_shouldReturnWalletWhenFound() {
        var wallet = Wallet.builder()
                .customerId(customerId)
                .assets(Collections.emptyList())
                .build();

        when(walletRepository.findByCustomerId(customerId)).thenReturn(Optional.of(wallet));

        Wallet result = service.findWalletByCustomerId(customerId);

        assertThat(result).isEqualTo(wallet);
        verify(walletRepository).findByCustomerId(customerId);
    }

    @Test
    void findWalletByCustomerId_shouldThrowExceptionWhenNotFound() {
        when(walletRepository.findByCustomerId(customerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findWalletByCustomerId(customerId))
                .isInstanceOf(WalletNotFoundException.class)
                .hasMessage("Wallet for customerId: %d not found".formatted(customerId));

        verify(walletRepository).findByCustomerId(customerId);
    }

    @Test
    void findAndAggregateAllWalletAssets_shouldReturnEmptyListWhenNoAssets() {
        var request = new WalletRequest(authHeader, customerId);
        var wallet = Wallet.builder()
                .customerId(customerId)
                .assets(null)
                .build();

        List<AssetDTO> result = service.findAndAggregateAllWalletAssets(wallet, request);

        assertThat(result).isEmpty();
    }

    @Test
    void findAndAggregateAllWalletAssets_shouldThrowOwnershipExceptionForCustomerIdMismatch() {
        Long otherCustomerId = 2L;
        var wallet = Wallet.builder()
                .customerId(otherCustomerId)
                .assets(List.of(Asset.builder().customerId(customerId).build()))
                .build();
        var request = new WalletRequest(authHeader, customerId);

        Throwable thrown = catchThrowable(() -> service.findAndAggregateAllWalletAssets(wallet, request));
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
        var request = WalletRequest.builder()
                .authorizationHeader(authHeader)
                .customerId(customerId)
                .build();
        var bitcoinResponse = FinanceResponse.builder()
                .name("Bitcoin")
                .priceUsd(BigDecimal.valueOf(45_000))
                .build();
        var ethereumResponse = FinanceResponse.builder()
                .name("Ethereum")
                .priceUsd(BigDecimal.valueOf(3000))
                .build();

        when(financeClient.findFinanceByUri("Bitcoin")).thenReturn(Optional.of(bitcoinResponse));
        when(financeClient.findFinanceByUri("Ethereum")).thenReturn(Optional.of(ethereumResponse));
        List<AssetDTO> result = service.findAndAggregateAllWalletAssets(wallet, request);

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

        when(financeClient.findFinanceByUri(uri)).thenReturn(Optional.of(expectedFinance));

        FinanceResponse result = service.fetchFinance(uri);

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("Bitcoin");
        assertThat(result.priceUsd()).isEqualByComparingTo(BigDecimal.valueOf(50000));
        verify(financeClient).findFinanceByUri(uri);
    }

    @Test
    void fetchFinance_shouldThrowExceptionWhenFinanceNotFound() {
        String notExistingUri = "NonExistentFinance";
        when(financeClient.findFinanceByUri(notExistingUri)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.fetchFinance(notExistingUri))
                .isInstanceOf(FinanceNotFoundException.class)
                .hasMessage("Finance not found for URI: NonExistentFinance");
        verify(financeClient).findFinanceByUri(notExistingUri);
    }

    @Test
    void deleteWallet_shouldDeleteWalletWhenValid() {
        var wallet = Wallet.builder()
                .customerId(customerId)
                .assets(Collections.emptyList())
                .build();
        var customer = CustomerResponse.builder()
                .id(customerId)
                .firstName("John")
                .lastName("Doe")
                .build();
        var request = WalletRequest.builder()
                .authorizationHeader(authHeader)
                .customerId(customerId)
                .build();

        when(walletRepository.findByCustomerId(customerId)).thenReturn(Optional.of(wallet));
        when(customerClient.findCustomerById(authHeader, customerId)).thenReturn(Optional.of(customer));

        service.deleteWallet(authHeader, request);

        verify(walletRepository).findByCustomerId(customerId);
        verify(customerClient).findCustomerById(authHeader, customerId);
        verify(walletRepository).delete(wallet);
    }

    @Test
    void deleteWallet_shouldThrowExceptionWhenCustomerNotFound() {
        var wallet = Wallet.builder()
                .customerId(customerId)
                .assets(Collections.emptyList())
                .build();
        var request = WalletRequest.builder()
                .authorizationHeader(authHeader)
                .customerId(customerId)
                .build();

        when(walletRepository.findByCustomerId(customerId)).thenReturn(Optional.of(wallet));
        when(customerClient.findCustomerById(authHeader, customerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteWallet(authHeader, request))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessage("Wallet not deleted:: No Customer exists with ID: %d".formatted(customerId));

        verify(walletRepository).findByCustomerId(customerId);
        verify(customerClient).findCustomerById(authHeader, customerId);
        verify(walletRepository, never()).delete(any());
    }

    @Test
    void deleteWallet_shouldThrowExceptionWhenOwnershipMismatch() {
        Long otherCustomerId = 2L;
        var wallet = Wallet.builder()
                .customerId(customerId)
                .assets(Collections.emptyList())
                .build();
        var customer = CustomerResponse.builder()
                .id(otherCustomerId)
                .firstName("Ela")
                .lastName("Smith")
                .build();
        var request = WalletRequest.builder()
                .authorizationHeader(authHeader)
                .customerId(customerId)
                .build();

        when(walletRepository.findByCustomerId(customerId)).thenReturn(Optional.of(wallet));
        when(customerClient.findCustomerById(authHeader, customerId)).thenReturn(Optional.of(customer));

        assertThatThrownBy(() -> service.deleteWallet(authHeader, request))
                .isInstanceOf(OwnershipException.class)
                .hasMessage("Wallet deletion failed:: Customer id mismatch");

        verify(walletRepository).findByCustomerId(customerId);
        verify(customerClient).findCustomerById(authHeader, customerId);
        verify(walletRepository, never()).delete(any());
    }

    @Test
    void deleteWallet_shouldThrowExceptionWhenWalletNotFound() {
        var request = WalletRequest.builder()
                .authorizationHeader(authHeader)
                .customerId(customerId)
                .build();

        when(walletRepository.findByCustomerId(customerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteWallet(authHeader, request))
                .isInstanceOf(WalletNotFoundException.class)
                .hasMessage("Wallet for customerId: %d not found".formatted(customerId) );

        verify(walletRepository).findByCustomerId(customerId);
        verify(customerClient, never()).findCustomerById(any(), any());
        verify(walletRepository, never()).delete(any());
    }
}