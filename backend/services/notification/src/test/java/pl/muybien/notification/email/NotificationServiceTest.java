package pl.muybien.notification.email;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import pl.muybien.notification.NotificationService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

public class NotificationServiceTest {

    @InjectMocks
    private NotificationService notificationService;

    @Mock
    private JavaMailSender javaMailSender;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void sendNotification() {
        String userEmail = "user@example.com";
        String subject = "Test Subject";
        String message = "Test Message";

        notificationService.sendNotification(userEmail, subject, message);

        verify(javaMailSender).send(any(SimpleMailMessage.class));
    }
}
