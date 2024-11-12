package pl.muybien.subscriptionservice.finance.crypto.binancecoin;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.reactive.function.client.WebClient;
import pl.muybien.subscriptionservice.finance.FinanceComparator;
import pl.muybien.subscriptionservice.subscription.SubscriptionListManager;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BinanceServiceTest {

    @InjectMocks
    private BinanceService service;

    @Mock
    private BinanceRepository repository;

    @Mock
    private FinanceComparator financeComparator;

    @Mock
    private SubscriptionListManager subscriptionListManager;

    private Binance crypto;
    private OidcUser oidcUser;
    private String email = "test@example.com";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Set up local mocks and interactions
        WebClient.Builder localWebClientBuilder = mock(WebClient.Builder.class);
        WebClient localWebClient = mock(WebClient.class);
        when(localWebClientBuilder.baseUrl(anyString())).thenReturn(localWebClientBuilder);
        when(localWebClientBuilder.build()).thenReturn(localWebClient);

        // Inject the local mock into the service instance
        service = new BinanceService(financeComparator, subscriptionListManager, repository, localWebClientBuilder);

        crypto = Binance.builder()
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
    void createAndSaveSubscription() {
        service.createAndSaveSubscription(email, crypto.getUpperBoundPrice(), crypto.getLowerBoundPrice());

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