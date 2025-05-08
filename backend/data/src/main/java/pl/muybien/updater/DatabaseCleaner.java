package pl.muybien.updater;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.muybien.finance.FinanceRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseCleaner extends QueueUpdater {
    private final FinanceRepository repository;

    @Scheduled(cron = "0 0 4 * * ?")
    @EventListener(ApplicationReadyEvent.class)
    @Override
    public void scheduleUpdate() {
        enqueueUpdate("database-cleaner");
    }

    @Override
    public void updateAssets() {
        log.info("Starting cleanup of old finance details...");

        repository.findAll().forEach(finance -> {
            finance.cleanOldFinanceDetails();
            repository.save(finance);
        });

        log.info("Old finance details cleaned successfully.");
    }
}
