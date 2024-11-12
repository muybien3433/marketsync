package pl.muybien.subscriptionservice.subscription;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.muybien.subscriptionservice.finance.FinanceTarget;
import pl.muybien.subscriptionservice.handler.SubscriptionDeletionException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class SubscriptionListManagerTest {

    @InjectMocks
    SubscriptionListManager subscriptionListManager;

    @Mock
    SubscriptionRepository subscriptionRepository;

    @Mock
    SubscriptionDTOMapper subscriptionDTOMapper;

    @Mock
    FinanceTarget financeTarget;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

//    @Test
//    void addSubscriptionToList() {
//        String email = "customer@example.com";
//        when(financeTarget.getId()).thenReturn(1L);
//        when(financeTarget.getUpperBoundPrice()).thenReturn(BigDecimal.valueOf(50000.0));
//        when(financeTarget.getLowerBoundPrice()).thenReturn(BigDecimal.valueOf(30000.0));
//        when(financeTarget.getName()).thenReturn("Bitcoin");
//        when(financeTarget.getCustomerEmail()).thenReturn("customer@example.com");
//
//        subscriptionListManager.addSubscriptionToList(financeTarget);
//
//        verify(subscriptionRepository).save(argThat(subscription ->
//                subscription.getFinanceId().equals(financeTarget.getId()) &&
//                        subscription.getUpperBoundPrice().equals(financeTarget.getUpperBoundPrice()) &&
//                        subscription.getLowerBoundPrice().equals(financeTarget.getLowerBoundPrice()) &&
//                        subscription.getName().equals(financeTarget.getName()) &&
//                        subscription.getCustomerEmail().equals(email)
//        ));
//    }

    @Test
    void removeSubscriptionFromList() {
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

//    @Test
//    void findAllCustomerSubscriptions() {
//        String email = "test@example.com";
//        Subscription subscription = new Subscription();
//        SubscriptionDetail subscriptionDetail = mock(SubscriptionDetail.class);
//
//        when(subscriptionRepository.findAllByCustomerEmail(email)).thenReturn(Optional.of(subscription));
//        when(subscription.getSubscriptions()).thenReturn(List.of(subscriptionDetail));
//
//        List<SubscriptionDTO> result = subscriptionListManager.findAllCustomerSubscriptions(email);
//
//        assertEquals(Arrays.asList(subscription), result);  // Expecting dto2 first since it is more recent
//
////         Verify that mocks were called as expected
//        verify(subscriptionRepository).findAllByCustomerEmail(email);
//        verify(subscriptionDTOMapper).mapToDTO(subscription);
//    }
}