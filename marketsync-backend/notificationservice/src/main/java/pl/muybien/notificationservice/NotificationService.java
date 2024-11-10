package pl.muybien.notificationservice;

public interface NotificationService {
    void sendNotification(String email, String subject, String message);
}
