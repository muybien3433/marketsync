package pl.muybien.subscription;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.muybien.kafka.SubscriptionEmailConfirmation;
import pl.muybien.kafka.SubscriptionProducer;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionComparator {

    private final SubscriptionProducer subscriptionProducer;

    @Transactional
    public void priceMetSubscriptionCondition(Double price, SubscriptionDetail subscriptionDetail) {
        if (subscriptionDetail != null) {
            Double upperTargetPrice = subscriptionDetail.getUpperBoundPrice();
            Double lowerTargetPrice = subscriptionDetail.getLowerBoundPrice();

            if (upperTargetPrice != null) {
                if (price.compareTo(upperTargetPrice) >= 0) {
                    log.info("Subscription {} met condition at {}", subscriptionDetail.getUri(), upperTargetPrice);
                    sendNotificationToSpecifiedTopic(subscriptionDetail, price, upperTargetPrice);
                    return;
                }
            }

            if (lowerTargetPrice != null) {
                if (price.compareTo(lowerTargetPrice) <= 0) {
                    log.info("Subscription {} met condition at {}", subscriptionDetail.getUri(), lowerTargetPrice);
                    sendNotificationToSpecifiedTopic(subscriptionDetail, price, lowerTargetPrice);
                    return;
                }
            }
        } else {
            log.error("Subscription detail is null");
        }
    }

    private void sendNotificationToSpecifiedTopic(
            SubscriptionDetail subscriptionDetail, Double price, Double targetPrice) {
        if (subscriptionDetail.getNotificationType() == null) {
            throw new IllegalStateException("Notification type is null for subscription detail: " + subscriptionDetail);
        }

        switch (subscriptionDetail.getNotificationType()) {
            case EMAIL -> createEmailConfirmation(subscriptionDetail, price, targetPrice);
            default -> throw new IllegalStateException("Subscription type not recognized: "
                    + subscriptionDetail.getNotificationType());
        }
    }

    private void createEmailConfirmation(SubscriptionDetail subscriptionDetail, Double price, Double targetPrice) {
        var subscriptionEmailConfirmation = SubscriptionEmailConfirmation.builder()
                .email(subscriptionDetail.getCustomerEmail())
                .subject("Your %s subscription notification!".formatted(subscriptionDetail.getFinanceName()))
                .body("Current %s value reached bound at: %s, your bound was %s"
                        .formatted(subscriptionDetail.getFinanceName(), price, targetPrice))
                .build();

        subscriptionProducer.sendSubscriptionEmailNotification(subscriptionEmailConfirmation);
    }
}
