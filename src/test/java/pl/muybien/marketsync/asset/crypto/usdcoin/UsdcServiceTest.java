package pl.muybien.marketsync.asset.crypto.usdcoin;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import pl.muybien.marketsync.asset.crypto.Crypto;
import pl.muybien.marketsync.asset.AssetComparator;
import pl.muybien.marketsync.asset.crypto.CryptoProvider;
import pl.muybien.marketsync.customer.Customer;
import pl.muybien.marketsync.subscription.SubscriptionListManager;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UsdcServiceTest {

    @InjectMocks
    private UsdcService service;

    @Mock
    private CryptoProvider cryptoProvider;

    @Mock
    private UsdcRepository repository;

    @Mock
    private AssetComparator assetComparator;

    @Mock
    private SubscriptionListManager subscriptionListManager;

    private Usdc crypto;
    private OidcUser oidcUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        crypto = Usdc.builder()
                .id(1L)
                .name("crypto-example")
                .upperBoundPrice(new BigDecimal(74000))
                .lowerBoundPrice(new BigDecimal(50000))
                .customer(Customer.builder().email("test@example.com").build())
                .build();

        oidcUser = mock(OidcUser.class);
        when(oidcUser.getEmail()).thenReturn("test@example.com");
    }

    @Test
    void fetchCurrentStock() {
        BigDecimal cryptoPrice = new BigDecimal("5300.00");
        var crypto = mock(Crypto.class);
        var subscription1 = mock(Usdc.class);
        var subscription2 = mock(Usdc.class);

        when(crypto.getPriceUsd()).thenReturn(cryptoPrice);
        when(cryptoProvider.fetchAsset(anyString())).thenReturn(crypto);
        when(repository.findAll()).thenReturn(List.of(subscription1, subscription2));
        when(assetComparator.currentPriceMetSubscriptionCondition(cryptoPrice, subscription1)).thenReturn(true);
        when(assetComparator.currentPriceMetSubscriptionCondition(cryptoPrice, subscription2)).thenReturn(false);

        service.fetchCurrentStock();

        verify(repository, times(1)).findAll();
        verify(repository).delete(subscription1);
        verify(repository, never()).delete(subscription2);
    }

    @Test
    void createAndSaveSubscription() {
        var customer = mock(Customer.class);

        service.createAndSaveSubscription(customer, crypto.getName(),
                crypto.getUpperBoundPrice(), crypto.getLowerBoundPrice());

        verify(repository, times(1)).save(any());
    }

    @Test
    void removeSubscriptionSuccess() {
        when(repository.findById(crypto.getId())).thenReturn(Optional.of(crypto));

        service.removeSubscription(oidcUser, crypto.getId());

        verify(repository, times(1)).delete(crypto);
    }

    @Test
    void removeSubscriptionNotFound() {
        when(repository.findById(crypto.getId())).thenReturn(Optional.empty());

        EntityNotFoundException e = assertThrows(EntityNotFoundException.class, () ->
                service.removeSubscription(oidcUser, crypto.getId()));

        assertEquals("Subscription with id 1 not found.", e.getMessage());
    }

    @Test
    void removeSubscriptionAccessDenied() {
        var tempCrypto = crypto;
        crypto.getCustomer().setEmail("bad-email@example.com");
        when(repository.findById(crypto.getId())).thenReturn(Optional.of(tempCrypto));

        AccessDeniedException e = assertThrows(AccessDeniedException.class, () ->
                service.removeSubscription(oidcUser, crypto.getId()));

        assertEquals("You are not authorized to delete this subscription.", e.getMessage());
    }
}