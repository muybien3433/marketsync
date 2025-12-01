package pl.muybien;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
@EnableFeignClients
@EnableJpaAuditing
public class WalletApplication {
    static void main(String[] args) {
		SpringApplication.run(WalletApplication.class, args);
	}
}
