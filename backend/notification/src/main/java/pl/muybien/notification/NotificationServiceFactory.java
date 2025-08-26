package pl.muybien.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.muybien.enumeration.NotificationType;
import pl.muybien.exception.ServiceNotFoundException;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationServiceFactory {

    private final Map<String, NotificationService> notificationServices;

    public void sendMessage(NotificationType notificationType, String target, String body) {
        var service = notificationServices.get(notificationType.name().toLowerCase());
        if (service != null) {
            service.sendMessage(target, body);
        } else {
            throw new ServiceNotFoundException("Service %s not supported"
                    .formatted(notificationType.name()));
        }
    }
}
