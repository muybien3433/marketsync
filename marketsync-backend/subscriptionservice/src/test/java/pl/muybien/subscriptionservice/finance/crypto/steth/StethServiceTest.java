package pl.muybien.subscriptionservice.finance.crypto.steth;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import pl.muybien.subscriptionservice.finance.FinanceComparator;
import pl.muybien.subscriptionservice.finance.crypto.Crypto;
import pl.muybien.subscriptionservice.finance.crypto.CryptoProvider;
import pl.muybien.subscriptionservice.subscription.SubscriptionListManager;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class StethServiceTest {

    @InjectMocks
    private StethService service;

    @Mock
    private CryptoProvider cryptoProvider;

    @Mock
    private StethRepository repository;

    @Mock
    private FinanceComparator financeComparator;

    @Mock
    private SubscriptionListManager subscriptionListManager;

    private Steth crypto;
    private OidcUser oidcUser;
    private String email = "test@example.com";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        crypto = Steth.builder()
                .id(1L)
                .name("crypto-example")
                .upperBoundPrice(new BigDecimal(74000))
                .lowerBoundPrice(new BigDecimal(50000))
                .customerEmail(email)
                .build();

        oidcUser = mock(OidcUser.class);
        when(oidcUser.getEmail()).thenReturn(email);
    }

    @Test
    void fetchCurrentFinance() {
        BigDecimal cryptoPrice = new BigDecimal("5300.00");
        var crypto = mock(Crypto.class);
        var subscription1 = mock(Steth.class);
        var subscription2 = mock(Steth.class);

        when(crypto.getPriceUsd()).thenReturn(cryptoPrice);
        when(cryptoProvider.fetchFinance(anyString())).thenReturn(crypto);
        when(repository.findAll()).thenReturn(List.of(subscription1, subscription2));
        when(financeComparator.currentPriceMetSubscriptionCondition(cryptoPrice, subscription1)).thenReturn(true);
        when(financeComparator.currentPriceMetSubscriptionCondition(cryptoPrice, subscription2)).thenReturn(false);

        service.fetchCurrentFinance();

        verify(repository, times(1)).findAll();
        verify(repository).delete(subscription1);
        verify(repository, never()).delete(subscription2);
    }

    @Test
    void createAndSaveSubscription() {
        service.createAndSaveSubscription(email, crypto.getName(),
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
        crypto.setCustomerEmail("bad-email@example.com");
        when(repository.findById(crypto.getId())).thenReturn(Optional.of(tempCrypto));

        AccessDeniedException e = assertThrows(AccessDeniedException.class, () ->
                service.removeSubscription(oidcUser, crypto.getId()));

        assertEquals("You are not authorized to delete this subscription.", e.getMessage());
    }
}