package pl.muybien;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class UserApplication {

    static void main(String[] args) {
		SpringApplication.run(UserApplication.class, args);
	}
}

