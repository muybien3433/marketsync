package pl.muybien.marketsync.currency.crypto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import pl.muybien.marketsync.currency.CurrencyService;
import pl.muybien.marketsync.currency.CurrencyServiceFactory;
import pl.muybien.marketsync.customer.Customer;
import pl.muybien.marketsync.customer.CustomerService;
import pl.muybien.marketsync.handler.InvalidSubscriptionParametersException;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class CryptoSubscriptionManagerTest {

    @InjectMocks
    private CryptoSubscriptionManager cryptoSubscriptionManager;

    @Mock
    private CurrencyServiceFactory currencyServiceFactory;

    @Mock
    private CustomerService customerService;

    @Mock
    private CurrencyService currencyService;

    @Mock
    private CryptoCurrencyProvider cryptoCurrencyProvider;

    @Mock
    private OidcUser oidcUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addSubscription() {
        String uri = "cryptoUri";
        String cryptoName = "cryptoName";
        Double upperValueInPercent = 10.0;
        Double lowerValueInPercent = -10.0;
        BigDecimal currentPrice = BigDecimal.valueOf(1000);
        BigDecimal expectedUpperPrice = BigDecimal.valueOf(1100).setScale(2);
        BigDecimal expectedLowerPrice = BigDecimal.valueOf(900).setScale(2);
        var customer = mock(Customer.class);
        var currentCrypto = mock(Crypto.class);

        when(currencyServiceFactory.getService(uri)).thenReturn(currencyService);
        when(cryptoCurrencyProvider.fetchCurrency(uri)).thenReturn(currentCrypto);
        when(currentCrypto.getName()).thenReturn(cryptoName);
        when(currentCrypto.getPriceUsd()).thenReturn(currentPrice);
        when(oidcUser.getEmail()).thenReturn("test@example.com");
        when(customerService.findCustomerByEmail("test@example.com")).thenReturn(customer);

        cryptoSubscriptionManager.addSubscription(oidcUser, uri, upperValueInPercent, lowerValueInPercent);

        verify(currencyServiceFactory).getService(uri);
        verify(cryptoCurrencyProvider).fetchCurrency(uri);
        verify(customerService).findCustomerByEmail("test@example.com");
        verify(currencyService).createAndSaveSubscription(customer, cryptoName, expectedUpperPrice, expectedLowerPrice);
    }

    @Test
    void addSubscriptionUpperValueNull() {
        String uri = "cryptoUri";
        String cryptoName = "cryptoName";
        Double lowerValueInPercent = -10.0;
        BigDecimal currentPrice = BigDecimal.valueOf(1000);
        BigDecimal expectedLowerPrice = BigDecimal.valueOf(900).setScale(2);
        var customer = mock(Customer.class);
        var currentCrypto = mock(Crypto.class);

        when(currencyServiceFactory.getService(uri)).thenReturn(currencyService);
        when(cryptoCurrencyProvider.fetchCurrency(uri)).thenReturn(currentCrypto);
        when(currentCrypto.getName()).thenReturn(cryptoName);
        when(currentCrypto.getPriceUsd()).thenReturn(currentPrice);
        when(oidcUser.getEmail()).thenReturn("test@example.com");
        when(customerService.findCustomerByEmail("test@example.com")).thenReturn(customer);

        cryptoSubscriptionManager.addSubscription(oidcUser, uri, null, lowerValueInPercent);

        verify(currencyServiceFactory).getService(uri);
        verify(cryptoCurrencyProvider).fetchCurrency(uri);
        verify(customerService).findCustomerByEmail("test@example.com");
        verify(currencyService).createAndSaveSubscription(customer, cryptoName, null, expectedLowerPrice);
    }

    @Test
    void addSubscriptionBothValuesNull() {
        String uri = "cryptoUri";
        String cryptoName = "cryptoName";
        BigDecimal currentPrice = BigDecimal.valueOf(1000);
        var customer = mock(Customer.class);
        var currentCrypto = mock(Crypto.class);

        when(currencyServiceFactory.getService(uri)).thenReturn(currencyService);
        when(cryptoCurrencyProvider.fetchCurrency(uri)).thenReturn(currentCrypto);
        when(currentCrypto.getName()).thenReturn(cryptoName);
        when(currentCrypto.getPriceUsd()).thenReturn(currentPrice);
        when(oidcUser.getEmail()).thenReturn("test@example.com");
        when(customerService.findCustomerByEmail("test@example.com")).thenReturn(customer);

        InvalidSubscriptionParametersException e = assertThrows(InvalidSubscriptionParametersException.class, () ->
                cryptoSubscriptionManager.addSubscription(oidcUser, uri, null, null));

        assertEquals("At least one parameter must be provided.", e.getMessage());

        verify(currencyServiceFactory).getService(uri);
        verify(cryptoCurrencyProvider).fetchCurrency(uri);
        verify(customerService).findCustomerByEmail("test@example.com");
        verify(currencyService, never()).createAndSaveSubscription(customer, cryptoName, null, null);
    }

    @Test
    void addSubscriptionLowerValueNull() {
        String uri = "cryptoUri";
        String cryptoName = "cryptoName";
        Double upperValueInPercent = 10.0;
        BigDecimal currentPrice = BigDecimal.valueOf(1000);
        BigDecimal expectedUpperPrice = BigDecimal.valueOf(1100).setScale(2);
        var customer = mock(Customer.class);
        var currentCrypto = mock(Crypto.class);

        when(currencyServiceFactory.getService(uri)).thenReturn(currencyService);
        when(cryptoCurrencyProvider.fetchCurrency(uri)).thenReturn(currentCrypto);
        when(currentCrypto.getName()).thenReturn(cryptoName);
        when(currentCrypto.getPriceUsd()).thenReturn(currentPrice);
        when(oidcUser.getEmail()).thenReturn("test@example.com");
        when(customerService.findCustomerByEmail("test@example.com")).thenReturn(customer);

        cryptoSubscriptionManager.addSubscription(oidcUser, uri, upperValueInPercent, null);

        verify(currencyServiceFactory).getService(uri);
        verify(cryptoCurrencyProvider).fetchCurrency(uri);
        verify(customerService).findCustomerByEmail("test@example.com");
        verify(currencyService).createAndSaveSubscription(customer, cryptoName, expectedUpperPrice, null);
    }

    @Test
    void removeSubscription() {
        Long subscriptionId = 1L;
        String uri = "cryptoUri";

        when(currencyServiceFactory.getService(uri)).thenReturn(currencyService);

        cryptoSubscriptionManager.removeSubscription(oidcUser, uri, subscriptionId);

        verify(currencyServiceFactory).getService(uri);
        verify(currencyService, times(1)).removeSubscription(oidcUser, subscriptionId);
    }
}