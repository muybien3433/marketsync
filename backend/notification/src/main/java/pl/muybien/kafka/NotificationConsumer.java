package pl.muybien.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import pl.muybien.alert.SupportNotification;
import pl.muybien.kafka.confirmation.SubscriptionConfirmation;
import pl.muybien.kafka.confirmation.SupportConfirmation;
import pl.muybien.notification.NotificationServiceFactory;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {

    private final NotificationServiceFactory notificationService;
    private final SupportNotification supportNotification;

    @KafkaListener(topics = "support-notification-topic")
    public void consumerSupportNotification(SupportConfirmation supportConfirmation) {
        log.info("Consuming body from support-topic");
        supportNotification.sendMessage(
                supportConfirmation.teamType(),
                supportConfirmation.alertType(),
                supportConfirmation.body()
        );
    }

    @KafkaListener(topics = "subscription-notification-topic")
    public void consumeSubscriptionSuccessNotification(SubscriptionConfirmation subscriptionConfirmation) {
        log.info("Consuming body from subscription-topic");

        notificationService.sendMessage(
                subscriptionConfirmation.notificationType(),
                subscriptionConfirmation.target(),
                subscriptionConfirmation.message()
        );
    }
}
