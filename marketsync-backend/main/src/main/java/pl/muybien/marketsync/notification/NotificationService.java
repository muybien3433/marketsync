package pl.muybien.marketsync.notification;

public interface NotificationService {
    void sendNotification(String email, String subject, String message);
}
