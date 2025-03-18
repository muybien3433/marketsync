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

    // TODO: create separate topics for email, phone etc.
    public void sendSubscriptionEmailNotification(SubscriptionConfirmation subscriptionConfirmation) {
        Message<SubscriptionConfirmation> message = MessageBuilder
                .withPayload(subscriptionConfirmation)
                .setHeader(KafkaHeaders.TOPIC, "subscription-email-notification-topic")
                .build();

        kafkaTemplate.send(message);
    }
}
