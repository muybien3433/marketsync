package pl.muybien.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import pl.muybien.NotificationServiceFactory;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {

    private final NotificationServiceFactory notificationService;

    @KafkaListener(topics = "subscription-notification-topic")
    public void consumeSubscriptionSuccessNotification(SubscriptionConfirmation subscriptionConfirmation) {
        log.info("Consuming message from subscription-topic");

        notificationService.sendMessage(
                subscriptionConfirmation.notificationType(),
                subscriptionConfirmation.target(),
                subscriptionConfirmation.message()
        );
    }
}
