package pl.muybien.updater;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.muybien.finance.FinanceDetail;
import pl.muybien.finance.FinanceRepository;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseCleaner extends QueueUpdater {
    private final FinanceRepository repository;

    @Scheduled(cron = "0 0 4 * * ?")
    @Override
    public void scheduleUpdate() {
        enqueueUpdate("database-cleaner");
    }

    @Override
    public void updateAssets() {
        log.info("Starting cleanup of old finance details...");

        List<FinanceDetail> allRemovedDetails = new ArrayList<>();

        repository.findAll().forEach(finance -> {
            List<FinanceDetail> removedDetails = finance.cleanOldFinanceDetails();
            allRemovedDetails.addAll(removedDetails);
            repository.save(finance);
        });

        log.info("Old finance details cleaned successfully. Total removed: {}", allRemovedDetails.size());

        allRemovedDetails.forEach(detail ->
                log.debug("Removed FinanceDetail: {}", detail)
        );
    }
}
