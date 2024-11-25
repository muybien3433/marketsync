package pl.muybien.notification.notification.email.event;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import pl.muybien.core.event.EmailSentEvent;
import pl.muybien.notification.notification.email.EmailService;

@Component
@KafkaListener(topics = "send-email-topic", containerFactory = "kafkaListenerContainerFactory")
@RequiredArgsConstructor
public class SendEmailRequestedEventHandler {

    private final static Logger LOGGER = LoggerFactory.getLogger(SendEmailRequestedEventHandler.class);
    private final EmailService emailService;

    @KafkaHandler
    public void handle(@Payload EmailSentEvent event) {
        String customerEmail = event.getCustomerEmail();
        String subject = event.getSubject();
        String body = event.getBody();

        emailService.sendNotification(customerEmail, subject, body);
        LOGGER.info("Received event: customerEmail: %s, subject: %s, body: %s".formatted(
                customerEmail,
                subject,
                body
        ));
    }
}
