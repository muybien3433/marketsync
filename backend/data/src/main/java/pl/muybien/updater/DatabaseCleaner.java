package pl.muybien.updater;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.muybien.finance.FinanceRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseCleaner {
    private final FinanceRepository repository;

    @Scheduled(cron = "0 0 4 * * ?")
    public void cleanOldFinanceDetails() {
        log.info("Starting cleanup of old finance details...");

        repository.findAll().forEach(finance -> {
            finance.cleanOldFinanceDetails();
            repository.save(finance);
        });

        log.info("Old finance details cleaned successfully.");
    }
}
