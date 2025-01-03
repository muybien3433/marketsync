package pl.muybien.config.api;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "crypto")
@Getter
@Setter
public class ApiProperties {
    private Map<String, CryptoConfig> api;
}
