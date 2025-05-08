package pl.muybien.kafka.producer;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import pl.muybien.kafka.confirmation.SubscriptionConfirmation;

@Component
public class SubscriptionProducer {

    @Qualifier("subscriptionKafkaTemplate")
    private final KafkaTemplate<String, SubscriptionConfirmation> kafkaTemplate;

    public SubscriptionProducer(KafkaTemplate<String, SubscriptionConfirmation> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendSubscriptionNotification(SubscriptionConfirmation subscriptionConfirmation) {
        Message<SubscriptionConfirmation> message = MessageBuilder
                .withPayload(subscriptionConfirmation)
                .setHeader(KafkaHeaders.TOPIC, "subscription-notification-topic")
                .build();

        kafkaTemplate.send(message);
    }
}
