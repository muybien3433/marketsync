package pl.muybien.subscription;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.muybien.exception.InvalidSubscriptionParametersException;
import pl.muybien.kafka.SubscriptionEmailConfirmation;
import pl.muybien.kafka.SubscriptionProducer;
import pl.muybien.subscription.data.SubscriptionDetail;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionComparator {

    private final SubscriptionProducer subscriptionProducer;

    @Transactional
    public void priceMetSubscriptionCondition(Double price, SubscriptionDetail subscriptionDetail) {
        if (subscriptionDetail != null) {
            Double upperTargetPrice = subscriptionDetail.upperBoundPrice();
            Double lowerTargetPrice = subscriptionDetail.lowerBoundPrice();

            if (upperTargetPrice != null) {
                if (price.compareTo(upperTargetPrice) >= 0) {
                    sendNotificationToSpecifiedTopic(subscriptionDetail, price, upperTargetPrice);
                    return;
                }
            }

            if (lowerTargetPrice != null) {
                if (price.compareTo(lowerTargetPrice) <= 0) {
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

        switch (subscriptionDetail.notificationType().toUpperCase()) {
            case "EMAIL" -> createEmailConfirmation(subscriptionDetail, price, targetPrice);
            default -> throw new InvalidSubscriptionParametersException(
                    "Subscription notification type not supported: " + subscriptionDetail.notificationType());
        }
    }

    private void createEmailConfirmation(SubscriptionDetail subscriptionDetail, Double price, Double targetPrice) {
        var subscriptionEmailConfirmation = SubscriptionEmailConfirmation.builder()
                .email(subscriptionDetail.customerEmail())
                .subject("Your %s subscription notification!".formatted(subscriptionDetail.financeName()))
                .body("Current %s value reached bound at: %s, your bound was %s"
                        .formatted(subscriptionDetail.financeName(), price, targetPrice))
                .build();

        subscriptionProducer.sendSubscriptionEmailNotification(subscriptionEmailConfirmation);
    }
}
