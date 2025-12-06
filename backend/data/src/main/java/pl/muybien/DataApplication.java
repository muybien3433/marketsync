package pl.muybien;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan(
        basePackages = "pl.muybien",
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.REGEX,
                pattern = "pl\\.muybien\\.kafka\\..*"
        )
)
@EnableScheduling
@EnableAsync
public class DataApplication {
    static void main(String[] args) {
		SpringApplication.run(DataApplication.class, args);
	}
}
