package pl.muybien.subscription;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.muybien.exception.InvalidSubscriptionParametersException;
import pl.muybien.kafka.SubscriptionEmailConfirmation;
import pl.muybien.kafka.SubscriptionProducer;
import pl.muybien.subscription.data.SubscriptionDetail;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SubscriptionComparatorTest {

    @Mock
    private SubscriptionProducer subscriptionProducer;

    @InjectMocks
    private SubscriptionComparator subscriptionComparator;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldSendEmailNotificationWhenUpperBoundPriceIsMet() {
        var subscriptionDetail = SubscriptionDetail.builder()
                .upperBoundPrice(100.0)
                .lowerBoundPrice(null)
                .notificationType("eMaIl")
                .customerEmail("test@example.com")
                .financeName("Bitcoin")
                .build();

        subscriptionComparator.priceMetSubscriptionCondition(120.0, subscriptionDetail);

        ArgumentCaptor<SubscriptionEmailConfirmation> captor =
                ArgumentCaptor.forClass(SubscriptionEmailConfirmation.class);
        verify(subscriptionProducer).sendSubscriptionEmailNotification(captor.capture());
        SubscriptionEmailConfirmation emailConfirmation = captor.getValue();

        assertNotNull(emailConfirmation);
        assertEquals("test@example.com", emailConfirmation.email());
        assertEquals("Your Bitcoin subscription notification!", emailConfirmation.subject());
        assertEquals(
                "Current Bitcoin value reached bound at: 120.0, your bound was 100.0",
                emailConfirmation.body()
        );
    }

    @Test
    void shouldSendEmailNotificationWhenLowerBoundPriceIsMet() {
        var subscriptionDetail = SubscriptionDetail.builder()
                .upperBoundPrice(null)
                .lowerBoundPrice(50.0)
                .notificationType("EMAIL")
                .customerEmail("test@example.com")
                .financeName("Bitcoin")
                .build();

        subscriptionComparator.priceMetSubscriptionCondition(40.0, subscriptionDetail);

        ArgumentCaptor<SubscriptionEmailConfirmation> captor =
                ArgumentCaptor.forClass(SubscriptionEmailConfirmation.class);
        verify(subscriptionProducer).sendSubscriptionEmailNotification(captor.capture());
        SubscriptionEmailConfirmation emailConfirmation = captor.getValue();

        assertNotNull(emailConfirmation);
        assertEquals("test@example.com", emailConfirmation.email());
        assertEquals("Your Bitcoin subscription notification!", emailConfirmation.subject());
        assertEquals(
                "Current Bitcoin value reached bound at: 40.0, your bound was 50.0",
                emailConfirmation.body()
        );
    }

    @Test
    void shouldNotSendNotificationWhenPriceDoesNotMeetBound() {
        var subscriptionDetail = SubscriptionDetail.builder()
                .upperBoundPrice(100.0)
                .lowerBoundPrice(null)
                .notificationType("email")
                .customerEmail("test@example.com")
                .financeName("Bitcoin")
                .build();

        subscriptionComparator.priceMetSubscriptionCondition(80.0, subscriptionDetail);

        verify(subscriptionProducer, times(0)).sendSubscriptionEmailNotification(any());
    }

    @Test
    void shouldThrowExceptionForInvalidNotificationType() {
        var subscriptionDetail = SubscriptionDetail.builder()
                .upperBoundPrice(100.0)
                .lowerBoundPrice(null)
                .notificationType("INVALID_TYPE")
                .customerEmail("test@example.com")
                .financeName("Bitcoin")
                .build();

        InvalidSubscriptionParametersException exception = assertThrows(
                InvalidSubscriptionParametersException.class,
                () -> subscriptionComparator.priceMetSubscriptionCondition(120.0, subscriptionDetail)
        );
        assertEquals("Subscription assetType not recognized: INVALID_TYPE", exception.getMessage());
    }
}
