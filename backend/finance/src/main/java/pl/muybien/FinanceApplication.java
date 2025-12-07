package pl.muybien;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class FinanceApplication {
    static void main(String[] args) {
        SpringApplication.run(FinanceApplication.class, args);
    }
}

