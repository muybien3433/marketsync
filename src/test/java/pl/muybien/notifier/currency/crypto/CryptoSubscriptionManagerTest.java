package pl.muybien.notifier.currency.crypto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import pl.muybien.notifier.customer.Customer;
import pl.muybien.notifier.customer.CustomerService;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;

class CryptoSubscriptionManagerTest {

    @InjectMocks
    private CryptoSubscriptionManager cryptoSubscriptionManager;

    @Mock
    private CryptoServiceFactory cryptoServiceFactory;

    @Mock
    private CustomerService customerService;

    @Mock
    private CryptoService cryptoService;

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

        when(cryptoServiceFactory.getService(uri)).thenReturn(cryptoService);
        when(cryptoCurrencyProvider.fetchCurrencyByUri(uri)).thenReturn(currentCrypto);
        when(currentCrypto.getName()).thenReturn(cryptoName);
        when(currentCrypto.getPriceUsd()).thenReturn(currentPrice);
        when(oidcUser.getEmail()).thenReturn("test@example.com");
        when(customerService.findCustomerByEmail("test@example.com")).thenReturn(customer);

        cryptoSubscriptionManager.addSubscription(oidcUser, uri, upperValueInPercent, lowerValueInPercent);

        verify(cryptoServiceFactory).getService(uri);
        verify(cryptoCurrencyProvider).fetchCurrencyByUri(uri);
        verify(customerService).findCustomerByEmail("test@example.com");
        verify(cryptoService).createAndSaveSubscription(customer, cryptoName, expectedUpperPrice, expectedLowerPrice);
    }

    @Test
    void removeSubscription() {
        Long subscriptionId = 1L;
        String uri = "cryptoUri";

        when(cryptoServiceFactory.getService(uri)).thenReturn(cryptoService);

        cryptoSubscriptionManager.removeSubscription(oidcUser, uri, subscriptionId);

        verify(cryptoServiceFactory).getService(uri);
        verify(cryptoService, times(1)).removeSubscription(oidcUser, subscriptionId);
    }
}