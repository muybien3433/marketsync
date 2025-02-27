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

import java.time.LocalDateTime;

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
        var subscriptionDetail = new SubscriptionDetail(
                "id",
                "uri",
                "customerId",
                "test@example.com",
                "Bitcoin",
                CurrencyType.USD.name(),
                100.0,
                null,
                AssetType.CRYPTOS.name(),
                NotificationType.EMAIL.name(),
                LocalDateTime.now()
        );

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
        var subscriptionDetail = new SubscriptionDetail(
                "id",
                "uri",
                "customerId",
                "test@example.com",
                "Bitcoin",
                CurrencyType.USD.name(),
                null,
                50.0,
                AssetType.CRYPTOS.name(),
                NotificationType.EMAIL.name(),
                LocalDateTime.now()
        );

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
        var subscriptionDetail = new SubscriptionDetail(
                "id",
                "uri",
                "customerId",
                "customerEmail",
                "Bitcoin",
                CurrencyType.USD.name(),
                100.0,
                null,
                AssetType.CRYPTOS.name(),
                NotificationType.EMAIL.name(),
                LocalDateTime.now()
        );

        subscriptionComparator.priceMetSubscriptionCondition(80.0, subscriptionDetail);

        verify(subscriptionProducer, times(0)).sendSubscriptionEmailNotification(any());
    }

    @Test
    void shouldThrowExceptionForInvalidNotificationType() {
        var subscriptionDetail = new SubscriptionDetail(
                "id",
                "uri",
                "customerId",
                "customerEmail",
                "Bitcoin",
                CurrencyType.USD.name(),
                100.0,
                null,
                AssetType.CRYPTOS.name(),
                "INVALID_TYPE",
                LocalDateTime.now()
        );

        InvalidSubscriptionParametersException exception = assertThrows(
                InvalidSubscriptionParametersException.class,
                () -> subscriptionComparator.priceMetSubscriptionCondition(120.0, subscriptionDetail)
        );
        assertEquals("Subscription notification type not supported: INVALID_TYPE", exception.getMessage());
    }
}
