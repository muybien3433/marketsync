package pl.muybien.alert;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.muybien.enumeration.AlertType;
import pl.muybien.enumeration.NotificationType;
import pl.muybien.enumeration.TeamType;
import pl.muybien.exception.ErrorResponse;
import pl.muybien.exception.SupportDetailsNotFoundException;
import pl.muybien.notification.NotificationServiceFactory;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class SupportNotification {

    private final NotificationServiceFactory notificationService;
    private final SupportNotificationProperties supportNotificationProperties;

    public void sendMessage(TeamType teamType, AlertType alertType, Object body) {
        ContactDetail recipients = getContactDetail(teamType, alertType);
        if (recipients != null && recipients.contactDetail().isEmpty()) {
            log.warn("No recipients found for team {} and alert type {}", teamType, alertType);
            return;
        }

        if (recipients == null) {
            throw new SupportDetailsNotFoundException("No support details found for team %s and alert type %s");
        }

        for (String contact : recipients.contactDetail()) {
            NotificationType notificationType = recipients.notificationType();
            String message = "";
            if (body instanceof ErrorResponse(String timestamp, int status, String error, String code, String path)) {
                message = String.format(
                        "Error occurred at %s. Status: %d, Error: %s, Code: %s, Path: %s",
                        timestamp,
                        status,
                        error,
                        code,
                        path
                );
            } else {
                message = body.toString();
            }

            if (!message.isEmpty()) {
                log.info("Sent {} alert via {} to {}: {}", alertType, notificationType, contact, body);
                notificationService.sendMessage(notificationType, contact, message);
            }
        }
    }

    private ContactDetail getContactDetail(TeamType teamType, AlertType alertType) {
        SupportNotificationProperties.TeamDetails teamDetails = switch (teamType) {
            case TECHNICS -> supportNotificationProperties.getTechnics();
            case SUPPORT -> supportNotificationProperties.getSupports();
            default -> null;
        };

        if (teamDetails == null) return null;

        return switch (alertType) {
            case INFO, WARNING, REQUEST -> new ContactDetail(teamDetails.getEmails(), NotificationType.EMAIL);
            case CRITICAL -> {
                List<String> numbers = teamDetails.getNumbers();
                if (numbers != null && !numbers.isEmpty()) {
                    yield new ContactDetail(numbers, NotificationType.SMS);
                } else {
                    log.warn("No phone numbers found for team {}, falling back to emails", teamType);
                    yield new ContactDetail(teamDetails.getEmails(), NotificationType.EMAIL);
                }
            }
        };
    }

    private record ContactDetail(
            List<String> contactDetail,
            NotificationType notificationType
    ){}
}
