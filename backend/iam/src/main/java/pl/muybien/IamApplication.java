package pl.muybien;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import pl.muybien.config.AccountSettingsProperties;

@SpringBootApplication
@EnableConfigurationProperties(AccountSettingsProperties.class)
public class IamApplication {

    static void main(String[] args) {
		SpringApplication.run(IamApplication.class, args);
	}
}
