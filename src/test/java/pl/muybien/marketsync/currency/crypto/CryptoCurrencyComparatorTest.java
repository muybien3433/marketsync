package pl.muybien.marketsync.currency.crypto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.muybien.marketsync.customer.Customer;
import pl.muybien.marketsync.notification.NotificationService;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class CryptoCurrencyComparatorTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private CryptoTarget subscription;

    @InjectMocks
    private CryptoCurrencyComparator cryptoCurrencyComparator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void currentPriceMetSubscriptionUpperBound() {
        BigDecimal currentPrice = new BigDecimal("8000");
        var customer = mock(Customer.class);

        when(subscription.getUpperBoundPrice()).thenReturn(new BigDecimal("7000"));
        when(subscription.getLowerBoundPrice()).thenReturn(new BigDecimal("6000"));
        when(subscription.getCustomer()).thenReturn(customer);

        boolean result = cryptoCurrencyComparator.currentPriceMetSubscriptionCondition(currentPrice, subscription);

        assertTrue(result);
        verify(notificationService, times(1)).sendNotification(any(), any(), any());
    }

    @Test
    void currentPriceMetSubscriptionLowerBound() {
        BigDecimal currentPrice = new BigDecimal("5000");
        var customer = mock(Customer.class);

        when(subscription.getUpperBoundPrice()).thenReturn(new BigDecimal("7000"));
        when(subscription.getLowerBoundPrice()).thenReturn(new BigDecimal("6000"));
        when(subscription.getCustomer()).thenReturn(customer);

        boolean result = cryptoCurrencyComparator.currentPriceMetSubscriptionCondition(currentPrice, subscription);

        assertTrue(result);
        verify(notificationService, times(1)).sendNotification(any(), any(), any());
    }

    @Test
    void currentPriceMetSubscriptionNoConditionMet() {
        BigDecimal currentPrice = new BigDecimal("6500");
        var customer = mock(Customer.class);

        when(subscription.getUpperBoundPrice()).thenReturn(new BigDecimal("7000"));
        when(subscription.getLowerBoundPrice()).thenReturn(new BigDecimal("6000"));
        when(subscription.getCustomer()).thenReturn(customer);

        boolean result = cryptoCurrencyComparator.currentPriceMetSubscriptionCondition(currentPrice, subscription);

        assertFalse(result);
        verify(notificationService, never()).sendNotification(any(), any(), any());
    }
}