package pl.muybien.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import pl.muybien.email.EmailService;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {

    private final EmailService emailService;

    @KafkaListener(topics = "subscription-email-notification-topic")
    public void consumeSubscriptionSuccessNotification(SubscriptionEmailConfirmation subscriptionEmailConfirmation) {
        log.info("Consuming message from subscription-topic {}", subscriptionEmailConfirmation);
        emailService.sendNotification(
                subscriptionEmailConfirmation.email(),
                subscriptionEmailConfirmation.subject(),
                subscriptionEmailConfirmation.body()
        );
    }
}
