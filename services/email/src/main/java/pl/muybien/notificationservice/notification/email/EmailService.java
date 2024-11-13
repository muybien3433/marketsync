package pl.muybien.notificationservice.notification.email;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import pl.muybien.notificationservice.NotificationService;

@Service
@RequiredArgsConstructor
public class EmailService implements NotificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationService.class);
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String companyEmail;

    @Override
    public void sendNotification(String customerEmail, String subject, String message) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(companyEmail);
        mailMessage.setTo(customerEmail);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);

        mailSender.send(mailMessage);
        LOGGER.info("Email sent to {}", customerEmail);
    }
}
