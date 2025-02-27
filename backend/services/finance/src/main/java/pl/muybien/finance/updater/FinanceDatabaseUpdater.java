package pl.muybien.finance.updater;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.muybien.finance.Finance;
import pl.muybien.finance.FinanceDetail;
import pl.muybien.finance.FinanceRepository;

import java.util.Map;

@Service
@RequiredArgsConstructor

public class FinanceDatabaseUpdater {

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
