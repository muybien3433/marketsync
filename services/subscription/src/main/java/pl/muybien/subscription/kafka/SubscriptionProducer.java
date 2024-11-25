package pl.muybien.subscription.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubscriptionProducer {

    private final KafkaTemplate<String, SubscriptionNotification> kafkaTemplate;

    public void sendSubscriptionNotification(SubscriptionNotification subscriptionNotification) {
        Message<SubscriptionNotification> message = MessageBuilder
                .withPayload(subscriptionNotification)
                .setHeader(KafkaHeaders.TOPIC, "subscription-topic")
                .build();

        kafkaTemplate.send(message);
    }
}
