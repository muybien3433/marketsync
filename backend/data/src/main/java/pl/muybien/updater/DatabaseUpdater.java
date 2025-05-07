package pl.muybien.updater;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.muybien.finance.Finance;
import pl.muybien.finance.FinanceDetail;
import pl.muybien.finance.FinanceRepository;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class DatabaseUpdater {

    private final FinanceRepository repository;

    @Transactional
    public void saveFinanceToDatabase(String assetType, Map<String, FinanceDetail> financeDetails) {
        String normalizedAssetType = assetType.toLowerCase();

        repository.findFinanceByAssetType(normalizedAssetType)
                .ifPresentOrElse(finance -> {
                            finance.initializeNestedMapIfNeeded(normalizedAssetType);
                            finance.getFinanceDetails().get(normalizedAssetType).putAll(financeDetails);
                            repository.save(finance);
                        },
                        () -> {
                            Finance newFinance = new Finance();
                            newFinance.initializeNestedMapIfNeeded(normalizedAssetType);
                            newFinance.getFinanceDetails().get(normalizedAssetType).putAll(financeDetails);
                            repository.save(newFinance);
                        }
                );
    }
}
