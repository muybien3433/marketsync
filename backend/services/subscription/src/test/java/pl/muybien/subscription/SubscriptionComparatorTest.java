package pl.muybien.subscription;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.muybien.kafka.SubscriptionProducer;
import pl.muybien.subscription.data.SubscriptionDetail;

import java.time.LocalDateTime;

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
    void shouldNotSendNotificationWhenPriceDoesNotMeetBound() {
        var subscriptionDetail = new SubscriptionDetail(
                "id",
                "uri",
                "customerId",
                "customerEmail",
                "Bitcoin",
                CurrencyType.USD,
                100.0,
                null,
                AssetType.CRYPTOS,
                NotificationType.EMAIL,
                LocalDateTime.now()
        );

        subscriptionComparator.priceMetSubscriptionConditionCheck(80.0, subscriptionDetail);

        verify(subscriptionProducer, times(0)).sendSubscriptionNotification(any());
    }
}
