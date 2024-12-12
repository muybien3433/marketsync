package pl.muybien.notification.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import pl.muybien.notification.email.EmailService;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {

    private final EmailService emailService;

    @KafkaListener(topics = "subscription-topic")
    public void consumeSubscriptionSuccessNotification(SubscriptionConfirmation subscriptionConfirmation) {
        log.info("Consuming message from subscription-topic {}", subscriptionConfirmation);
        emailService.sendNotification(
                subscriptionConfirmation.email(),
                subscriptionConfirmation.subject(),
                subscriptionConfirmation.body()
        );
    }
}
