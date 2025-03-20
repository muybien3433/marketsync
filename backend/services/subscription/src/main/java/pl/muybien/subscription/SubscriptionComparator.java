package pl.muybien.subscription;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.muybien.kafka.SubscriptionConfirmation;
import pl.muybien.kafka.SubscriptionProducer;
import pl.muybien.subscription.data.SubscriptionDetail;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionComparator {

    private final SubscriptionProducer subscriptionProducer;
    private final SubscriptionService subscriptionService;

    @Transactional
    public void priceMetSubscriptionConditionCheck(Double currentPrice, SubscriptionDetail subscriptionDetail) {
        if (subscriptionDetail != null) {
            Double upperTargetPrice = subscriptionDetail.upperBoundPrice();
            Double lowerTargetPrice = subscriptionDetail.lowerBoundPrice();

            if (upperTargetPrice != null) {
                if (currentPrice.compareTo(upperTargetPrice) >= 0) {
                    sendNotificationToSpecifiedTopic(subscriptionDetail, currentPrice, upperTargetPrice);
                }
            }

            if (lowerTargetPrice != null) {
                if (currentPrice.compareTo(lowerTargetPrice) <= 0) {
                    sendNotificationToSpecifiedTopic(subscriptionDetail, currentPrice, lowerTargetPrice);
                }
            }
        } else {
            log.error("Subscription detail is null");
        }
    }

    @Transactional
    public void sendNotificationToSpecifiedTopic(SubscriptionDetail detail, Double price, Double targetPrice) {
        String message = "Current %s value reached bound at: %s, your bound was %s"
                .formatted(detail.financeName(), price, targetPrice);
        var subscriptionConfirmation = new SubscriptionConfirmation(
                detail.notificationType(),
                detail.target(),
                message
        );
        subscriptionProducer.sendSubscriptionNotification(subscriptionConfirmation);
        subscriptionService.deleteSubscription(detail.customerId(), detail.uri(), detail.id());
    }
}
