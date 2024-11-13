package pl.muybien.subscriptionservice.finance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class FinanceComparatorTest {

//    @Mock
//    private NotificationService notificationService;

    @Mock
    private FinanceTarget subscription;

    @InjectMocks
    private FinanceComparator financeComparator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void currentPriceMetSubscriptionUpperBound() {
        BigDecimal currentPrice = new BigDecimal("8000");
        String email = "customer@example.com";

        when(subscription.getUpperBoundPrice()).thenReturn(new BigDecimal("7000"));
        when(subscription.getLowerBoundPrice()).thenReturn(new BigDecimal("6000"));
        when(subscription.getCustomerEmail()).thenReturn(email);

//        boolean result = financeComparator.priceMetSubscriptionCondition(currentPrice, subscription);

//        assertTrue(result);
//        verify(notificationService, times(1)).sendEmailNotification(any(), any(), any());
    }

    @Test
    void currentPriceMetSubscriptionLowerBound() {
        BigDecimal currentPrice = new BigDecimal("5000");
        String email = "customer@example.com";

        when(subscription.getUpperBoundPrice()).thenReturn(new BigDecimal("7000"));
        when(subscription.getLowerBoundPrice()).thenReturn(new BigDecimal("6000"));
        when(subscription.getCustomerEmail()).thenReturn(email);

//        boolean result = financeComparator.priceMetSubscriptionCondition(currentPrice, subscription);

//        assertTrue(result);
//        verify(notificationService, times(1)).sendEmailNotification(any(), any(), any());
    }

    @Test
    void currentPriceMetSubscriptionNoConditionMet() {
        BigDecimal currentPrice = new BigDecimal("6500");
        String email = "customer@example.com";

        when(subscription.getUpperBoundPrice()).thenReturn(new BigDecimal("7000"));
        when(subscription.getLowerBoundPrice()).thenReturn(new BigDecimal("6000"));
        when(subscription.getCustomerEmail()).thenReturn(email);

        boolean result = financeComparator.priceMetSubscriptionCondition(currentPrice, subscription);

        assertFalse(result);
//        verify(notificationService, never()).sendEmailNotification(any(), any(), any());
    }
}