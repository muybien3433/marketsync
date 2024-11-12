package pl.muybien.subscriptionservice.finance;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.muybien.core.event.EmailSentEvent;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class FinanceComparator {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Value("${send-email-topic}")
    private static String sendEmailTopic;

    @Transactional("transactionManager")
    public <T extends FinanceTarget> boolean priceMetSubscriptionCondition(BigDecimal currentPriceUsd,
                                                                           T subscription) {
        if (subscription != null) {
            BigDecimal upperTargetPrice = subscription.getUpperBoundPrice();
            BigDecimal lowerTargetPrice = subscription.getLowerBoundPrice();
            boolean currentValueEqualsOrGreaterThanTarget = currentPriceUsd.compareTo(upperTargetPrice) >= 0;
            boolean currentValueEqualsOrLowerThanTarget = currentPriceUsd.compareTo(lowerTargetPrice) <= 0;

            if (currentValueEqualsOrGreaterThanTarget) {
                sendNotification(subscription, currentPriceUsd, upperTargetPrice);
                return true;
            } else if (currentValueEqualsOrLowerThanTarget) {
                sendNotification(subscription, currentPriceUsd, lowerTargetPrice);
                return true;
            }
        }
        return false;
    }

    @Transactional("transactionManager")
    public <T extends FinanceTarget> void sendNotification(T subscription, BigDecimal currentPriceUsd,
                                                           BigDecimal targetPrice) {
        String email = subscription.getCustomerEmail();
        String subject = "Your %s subscription notification!".formatted(subscription.getName());
        String body = "Current %s value reached bound at: %s, your bound was %s".formatted(
                subscription.getName(), currentPriceUsd, targetPrice);

        EmailSentEvent emailSentEvent = new EmailSentEvent(email, subject, body);

        kafkaTemplate.send(sendEmailTopic, emailSentEvent);
        LOGGER.info("Send event {} to topic {}", emailSentEvent, sendEmailTopic);
    }
}
