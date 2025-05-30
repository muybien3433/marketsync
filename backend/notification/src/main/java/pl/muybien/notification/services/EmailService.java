package pl.muybien.notification.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.MailException;

import org.springframework.stereotype.Service;
import pl.muybien.exception.MessageNotSendException;
import pl.muybien.notification.NotificationService;

@Service("email")
@RequiredArgsConstructor
@Slf4j
public class EmailService implements NotificationService {

    private final JavaMailSender javaMailSender;

    @Override
    public void sendMessage(String target, String body) {
        var mimeMessage = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setFrom("noreply@muybien.pl");
            helper.setTo(target);
            helper.setSubject("MuyBien");

            String htmlContent = "<html><body><h1>Halo!</h1><p>" + body + "</p></body></html>";
            helper.setText(htmlContent, true);

            javaMailSender.send(mimeMessage);
        } catch (MailException | jakarta.mail.MessagingException e) {
            log.error(e.getMessage(), e);
            throw new MessageNotSendException(
                    "Message to %s with body %s could not be send".formatted(target, body));
        }
    }
}