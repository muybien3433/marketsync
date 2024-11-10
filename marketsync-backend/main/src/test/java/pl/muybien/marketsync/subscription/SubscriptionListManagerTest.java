package pl.muybien.marketsync.subscription;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.muybien.marketsync.customer.Customer;
import pl.muybien.marketsync.finance.FinanceTarget;
import pl.muybien.marketsync.handler.SubscriptionDeletionException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SubscriptionListManagerTest {

    @InjectMocks
    SubscriptionListManager subscriptionListManager;

    @Mock
    SubscriptionRepository subscriptionRepository;

    @Mock
    FinanceTarget financeTarget;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addSubscriptionToList() {
        Customer customer = mock(Customer.class);
        when(financeTarget.getId()).thenReturn(1L);
        when(financeTarget.getUpperBoundPrice()).thenReturn(BigDecimal.valueOf(50000.0));
        when(financeTarget.getLowerBoundPrice()).thenReturn(BigDecimal.valueOf(30000.0));
        when(financeTarget.getName()).thenReturn("Bitcoin");
        when(financeTarget.getCustomer()).thenReturn(customer);
        when(customer.getEmail()).thenReturn("customer@example.com");

        subscriptionListManager.addSubscriptionToList(financeTarget);

        verify(subscriptionRepository).save(argThat(subscription ->
                subscription.getFinanceId().equals(financeTarget.getId()) &&
                        subscription.getUpperBoundPrice().equals(financeTarget.getUpperBoundPrice()) &&
                        subscription.getLowerBoundPrice().equals(financeTarget.getLowerBoundPrice()) &&
                        subscription.getName().equals(financeTarget.getName()) &&
                        subscription.getCustomer().equals(financeTarget.getCustomer()) &&
                        subscription.getCustomerEmail().equals(customer.getEmail())
        ));
    }

    @Test
    void removeSubscriptionFromList() {
        when(financeTarget.getCustomer()).thenReturn(mock(Customer.class));
        when(financeTarget.getId()).thenReturn(5L);

        subscriptionListManager.removeSubscriptionFromList(financeTarget);

        verify(subscriptionRepository, times(1)).deleteByFinanceId(5L);
    }

    @Test
    void removeSubscriptionFromListException() {
        Long financeId = 1L;

        when(financeTarget.getId()).thenReturn(1L);
        when(financeTarget.getName()).thenReturn("Bitcoin");

        doThrow(new RuntimeException("Deletion failed"))
                .when(subscriptionRepository).deleteByFinanceId(financeId);

        SubscriptionDeletionException e = assertThrows(
                SubscriptionDeletionException.class,
                () -> subscriptionListManager.removeSubscriptionFromList(financeTarget)
        );

        String expectedMessage = "Subscription: Bitcoin id: 1 could not be deleted.";
        assertEquals(expectedMessage, e.getMessage());
    }

    @Test
    void findAllCustomerSubscriptions() {
        String email = "test@email.com";
        var subscription1 = mock(Subscription.class);
        var subscription2 = mock(Subscription.class);
        Optional<List<Subscription>> subscriptions = Optional.of(List.of(subscription1, subscription2));

        when(subscriptionRepository.findAllByCustomerEmail(email)).thenReturn(subscriptions);

        var result = subscriptionRepository.findAllByCustomerEmail(email);

        assertEquals(subscriptions, result);
        verify(subscriptionRepository, times(1)).findAllByCustomerEmail(email);
    }
}