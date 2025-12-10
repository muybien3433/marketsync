package pl.muybien.kafka;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import pl.muybien.kafka.confirmation.SubscriptionConfirmation;

import java.util.concurrent.TimeUnit;

@Component
public class SubscriptionProducer {

    private final KafkaTemplate<String, SubscriptionConfirmation> kafkaTemplate;

    public SubscriptionProducer(@Qualifier("subscriptionKafkaTemplate")
                                KafkaTemplate<String, SubscriptionConfirmation> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendSubscriptionNotification(SubscriptionConfirmation subscriptionConfirmation) {
        Message<SubscriptionConfirmation> message = MessageBuilder
                .withPayload(subscriptionConfirmation)
                .setHeader(KafkaHeaders.TOPIC, "subscription-notification-topic")
                .build();

        try {
            kafkaTemplate.send(message).get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send subscription notification", e);
        }
    }
}
