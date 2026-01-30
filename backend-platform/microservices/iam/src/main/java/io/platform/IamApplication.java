package io.platform;

import io.platform.config.AccountSettingsProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AccountSettingsProperties.class)
public class IamApplication {

    static void main(String[] args) {
		SpringApplication.run(IamApplication.class, args);
	}
}
