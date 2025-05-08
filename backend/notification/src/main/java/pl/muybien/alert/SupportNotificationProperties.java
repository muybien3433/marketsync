package pl.muybien.alert;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "support.notification")
@Getter
@Setter
public class SupportNotificationProperties {

    private TeamDetails technics;
    private TeamDetails supports;

    @Getter
    @Setter
    public static class TeamDetails {
        private List<String> emails;
        private List<String> numbers;
    }
}