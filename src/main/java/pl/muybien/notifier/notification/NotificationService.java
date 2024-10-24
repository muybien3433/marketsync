package pl.muybien.notifier.notification;

public interface NotificationService {
    void sendNotification(String email, String subject, String message);
}
