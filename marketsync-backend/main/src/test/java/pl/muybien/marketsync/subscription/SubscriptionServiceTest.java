package pl.muybien.marketsync.subscription;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import pl.muybien.marketsync.finance.*;
import pl.muybien.marketsync.finance.crypto.Crypto;
import pl.muybien.marketsync.customer.Customer;
import pl.muybien.marketsync.customer.CustomerService;
import pl.muybien.marketsync.handler.InvalidSubscriptionParametersException;

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
    private CustomerService customerService;

    @Mock
    private FinanceService financeService;

    @Mock
    private FinanceProvider financeProvider;

    @Mock
    private OidcUser oidcUser;

    private final String uri = "cryptoUri";
    private final String provider = "crypto";

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
        var customer = mock(Customer.class);
        var currentCrypto = Mockito.mock(Crypto.class);

        when(financeProviderFactory.getProvider(provider)).thenReturn(financeProvider);
        when(financeServiceFactory.getService(uri)).thenReturn(financeService);
        when(financeProvider.fetchFinance(uri)).thenReturn(currentCrypto);
        when(currentCrypto.getName()).thenReturn(cryptoName);
        when(currentCrypto.getPriceUsd()).thenReturn(currentPrice);
        when(oidcUser.getEmail()).thenReturn("test@example.com");
        when(customerService.findCustomerByEmail("test@example.com")).thenReturn(customer);

        subscriptionService.addSubscription(oidcUser, uri, upperValueInPercent, lowerValueInPercent);

        verify(financeServiceFactory).getService(uri);
        verify(financeProvider).fetchFinance(uri);
        verify(customerService).findCustomerByEmail("test@example.com");
        verify(financeService).createAndSaveSubscription(customer, cryptoName, expectedUpperPrice, expectedLowerPrice);
    }

    @Test
    void addSubscriptionUpperValueNull() {
        String cryptoName = "cryptoName";
        Double lowerValueInPercent = -10.0;
        BigDecimal currentPrice = BigDecimal.valueOf(1000);
        BigDecimal expectedLowerPrice = BigDecimal.valueOf(900).setScale(2);
        var customer = mock(Customer.class);
        var currentCrypto = mock(Crypto.class);

        when(financeProviderFactory.getProvider(provider)).thenReturn(financeProvider);
        when(financeServiceFactory.getService(uri)).thenReturn(financeService);
        when(financeProvider.fetchFinance(uri)).thenReturn(currentCrypto);
        when(currentCrypto.getName()).thenReturn(cryptoName);
        when(currentCrypto.getPriceUsd()).thenReturn(currentPrice);
        when(oidcUser.getEmail()).thenReturn("test@example.com");
        when(customerService.findCustomerByEmail("test@example.com")).thenReturn(customer);

        subscriptionService.addSubscription(oidcUser, uri, null, lowerValueInPercent);

        verify(financeServiceFactory).getService(uri);
        verify(financeProvider).fetchFinance(uri);
        verify(customerService).findCustomerByEmail("test@example.com");
        verify(financeService).createAndSaveSubscription(customer, cryptoName, null, expectedLowerPrice);
    }

    @Test
    void addSubscriptionBothValuesNull() {
        String cryptoName = "cryptoName";
        BigDecimal currentPrice = BigDecimal.valueOf(1000);
        var customer = mock(Customer.class);
        var currentCrypto = mock(Crypto.class);

        when(financeProviderFactory.getProvider(provider)).thenReturn(financeProvider);
        when(financeServiceFactory.getService(uri)).thenReturn(financeService);
        when(financeProvider.fetchFinance(uri)).thenReturn(currentCrypto);
        when(currentCrypto.getName()).thenReturn(cryptoName);
        when(currentCrypto.getPriceUsd()).thenReturn(currentPrice);
        when(oidcUser.getEmail()).thenReturn("test@example.com");
        when(customerService.findCustomerByEmail("test@example.com")).thenReturn(customer);

        InvalidSubscriptionParametersException e = assertThrows(InvalidSubscriptionParametersException.class, () ->
                subscriptionService.addSubscription(oidcUser, uri, null, null));

        assertEquals("At least one parameter must be provided.", e.getMessage());

        verify(financeServiceFactory).getService(uri);
        verify(financeProvider).fetchFinance(uri);
        verify(customerService).findCustomerByEmail("test@example.com");
        verify(financeService, never()).createAndSaveSubscription(customer, cryptoName, null, null);
    }

    @Test
    void addSubscriptionLowerValueNull() {
        String cryptoName = "cryptoName";
        Double upperValueInPercent = 10.0;
        BigDecimal currentPrice = BigDecimal.valueOf(1000);
        BigDecimal expectedUpperPrice = BigDecimal.valueOf(1100).setScale(2);
        var customer = mock(Customer.class);
        var currentCrypto = mock(Crypto.class);

        when(financeProviderFactory.getProvider(provider)).thenReturn(financeProvider);
        when(financeServiceFactory.getService(uri)).thenReturn(financeService);
        when(financeProvider.fetchFinance(uri)).thenReturn(currentCrypto);
        when(currentCrypto.getName()).thenReturn(cryptoName);
        when(currentCrypto.getPriceUsd()).thenReturn(currentPrice);
        when(oidcUser.getEmail()).thenReturn("test@example.com");
        when(customerService.findCustomerByEmail("test@example.com")).thenReturn(customer);

        subscriptionService.addSubscription(oidcUser, uri, upperValueInPercent, null);

        verify(financeServiceFactory).getService(uri);
        verify(financeProvider).fetchFinance(uri);
        verify(customerService).findCustomerByEmail("test@example.com");
        verify(financeService).createAndSaveSubscription(customer, cryptoName, expectedUpperPrice, null);
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