package pl.muybien.subscription;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
            Double upperTargetPrice = subscriptionDetail.getUpperBoundPrice();
            Double lowerTargetPrice = subscriptionDetail.getLowerBoundPrice();

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

    void sendNotificationToSpecifiedTopic(
            SubscriptionDetail subscriptionDetail, Double price, Double targetPrice) {

        var notificationType = SubscriptionNotificationType.findByValue(subscriptionDetail.getNotificationType());
        switch (notificationType) {
            case EMAIL -> createEmailConfirmation(subscriptionDetail, price, targetPrice);
        }
    }

    void createEmailConfirmation(SubscriptionDetail subscriptionDetail, Double price, Double targetPrice) {
        var subscriptionEmailConfirmation = SubscriptionEmailConfirmation.builder()
                .email(subscriptionDetail.getCustomerEmail())
                .subject("Your %s subscription notification!".formatted(subscriptionDetail.getFinanceName()))
                .body("Current %s value reached bound at: %s, your bound was %s"
                        .formatted(subscriptionDetail.getFinanceName(), price, targetPrice))
                .build();

        subscriptionProducer.sendSubscriptionEmailNotification(subscriptionEmailConfirmation);
    }
}
