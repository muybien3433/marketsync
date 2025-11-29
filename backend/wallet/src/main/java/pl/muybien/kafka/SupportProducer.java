package pl.muybien.kafka;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import pl.muybien.kafka.confirmation.SupportConfirmation;

@Component
public class SupportProducer {

    @Qualifier("supportKafkaTemplate")
    private final KafkaTemplate<String, SupportConfirmation> kafkaTemplate;

    public SupportProducer(KafkaTemplate<String, SupportConfirmation> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendNotification(SupportConfirmation supportConfirmation) {
        Message<SupportConfirmation> message = MessageBuilder
                .withPayload(supportConfirmation)
                .setHeader(KafkaHeaders.TOPIC, "support-notification-topic")
                .build();

        kafkaTemplate.send(message);
    }
}
