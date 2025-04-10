package pl.muybien.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubscriptionProducer {

    private final KafkaTemplate<String, SubscriptionConfirmation> kafkaTemplate;

    public void sendSubscriptionNotification(SubscriptionConfirmation subscriptionConfirmation) {
        Message<SubscriptionConfirmation> message = MessageBuilder
                .withPayload(subscriptionConfirmation)
                .setHeader(KafkaHeaders.TOPIC, "subscription-notification-topic")
                .build();

        kafkaTemplate.send(message);
    }
}
