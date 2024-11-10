package pl.muybien.subscriptionservice.subscription;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import pl.muybien.subscriptionservice.finance.FinanceProvider;
import pl.muybien.subscriptionservice.finance.FinanceProviderFactory;
import pl.muybien.subscriptionservice.finance.FinanceService;
import pl.muybien.subscriptionservice.finance.FinanceServiceFactory;
import pl.muybien.subscriptionservice.finance.crypto.Crypto;
import pl.muybien.subscriptionservice.handler.InvalidSubscriptionParametersException;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class SubscriptionServiceTest {

    @InjectMocks
    private SubscriptionService subscriptionService;

    @Mock
    private FinanceServiceFactory financeServiceFactory;

    @Mock
    private FinanceProviderFactory financeProviderFactory;

    @Mock
    private FinanceService financeService;

    @Mock
    private FinanceProvider financeProvider;

    @Mock
    private OidcUser oidcUser;

    private final String uri = "cryptoUri";
    private final String provider = "crypto";
    private final String email = "test@example.com";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addSubscription() {
        String cryptoName = "cryptoName";
        Double upperValueInPercent = 10.0;
        Double lowerValueInPercent = -10.0;
        BigDecimal currentPrice = BigDecimal.valueOf(1000);
        BigDecimal expectedUpperPrice = BigDecimal.valueOf(1100).setScale(2);
        BigDecimal expectedLowerPrice = BigDecimal.valueOf(900).setScale(2);
        var currentCrypto = Mockito.mock(Crypto.class);

        when(financeProviderFactory.getProvider(provider)).thenReturn(financeProvider);
        when(financeServiceFactory.getService(uri)).thenReturn(financeService);
        when(financeProvider.fetchFinance(uri)).thenReturn(currentCrypto);
        when(currentCrypto.getName()).thenReturn(cryptoName);
        when(currentCrypto.getPriceUsd()).thenReturn(currentPrice);
        when(oidcUser.getEmail()).thenReturn(email);

        subscriptionService.addSubscription(oidcUser, uri, upperValueInPercent, lowerValueInPercent);

        verify(financeServiceFactory).getService(uri);
        verify(financeProvider).fetchFinance(uri);
        verify(financeService).createAndSaveSubscription(email, cryptoName, expectedUpperPrice, expectedLowerPrice);
    }

    @Test
    void addSubscriptionUpperValueNull() {
        String cryptoName = "cryptoName";
        Double lowerValueInPercent = -10.0;
        BigDecimal currentPrice = BigDecimal.valueOf(1000);
        BigDecimal expectedLowerPrice = BigDecimal.valueOf(900).setScale(2);
        var currentCrypto = mock(Crypto.class);

        when(financeProviderFactory.getProvider(provider)).thenReturn(financeProvider);
        when(financeServiceFactory.getService(uri)).thenReturn(financeService);
        when(financeProvider.fetchFinance(uri)).thenReturn(currentCrypto);
        when(currentCrypto.getName()).thenReturn(cryptoName);
        when(currentCrypto.getPriceUsd()).thenReturn(currentPrice);
        when(oidcUser.getEmail()).thenReturn(email);

        subscriptionService.addSubscription(oidcUser, uri, null, lowerValueInPercent);

        verify(financeServiceFactory).getService(uri);
        verify(financeProvider).fetchFinance(uri);
        verify(financeService).createAndSaveSubscription(email, cryptoName, null, expectedLowerPrice);
    }

    @Test
    void addSubscriptionBothValuesNull() {
        String cryptoName = "cryptoName";
        BigDecimal currentPrice = BigDecimal.valueOf(1000);
        var currentCrypto = mock(Crypto.class);

        when(financeProviderFactory.getProvider(provider)).thenReturn(financeProvider);
        when(financeServiceFactory.getService(uri)).thenReturn(financeService);
        when(financeProvider.fetchFinance(uri)).thenReturn(currentCrypto);
        when(currentCrypto.getName()).thenReturn(cryptoName);
        when(currentCrypto.getPriceUsd()).thenReturn(currentPrice);
        when(oidcUser.getEmail()).thenReturn(email);

        InvalidSubscriptionParametersException e = assertThrows(InvalidSubscriptionParametersException.class, () ->
                subscriptionService.addSubscription(oidcUser, uri, null, null));

        assertEquals("At least one parameter must be provided.", e.getMessage());

        verify(financeServiceFactory).getService(uri);
        verify(financeProvider).fetchFinance(uri);
        verify(financeService, never()).createAndSaveSubscription(email, cryptoName, null, null);
    }

    @Test
    void addSubscriptionLowerValueNull() {
        String cryptoName = "cryptoName";
        Double upperValueInPercent = 10.0;
        BigDecimal currentPrice = BigDecimal.valueOf(1000);
        BigDecimal expectedUpperPrice = BigDecimal.valueOf(1100).setScale(2);
        var currentCrypto = mock(Crypto.class);

        when(financeProviderFactory.getProvider(provider)).thenReturn(financeProvider);
        when(financeServiceFactory.getService(uri)).thenReturn(financeService);
        when(financeProvider.fetchFinance(uri)).thenReturn(currentCrypto);
        when(currentCrypto.getName()).thenReturn(cryptoName);
        when(currentCrypto.getPriceUsd()).thenReturn(currentPrice);
        when(oidcUser.getEmail()).thenReturn(email);

        subscriptionService.addSubscription(oidcUser, uri, upperValueInPercent, null);

        verify(financeServiceFactory).getService(uri);
        verify(financeProvider).fetchFinance(uri);
        verify(financeService).createAndSaveSubscription(email, cryptoName, expectedUpperPrice, null);
    }

    @Test
    void removeSubscription() {
        Long subscriptionId = 1L;

        when(financeProviderFactory.getProvider(provider)).thenReturn(financeProvider);
        when(financeServiceFactory.getService(uri)).thenReturn(financeService);

        subscriptionService.removeSubscription(oidcUser, uri, subscriptionId);

        verify(financeServiceFactory).getService(uri);
        verify(financeService, times(1)).removeSubscription(oidcUser, subscriptionId);
    }
}