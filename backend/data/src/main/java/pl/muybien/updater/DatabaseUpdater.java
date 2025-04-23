package pl.muybien.updater;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.muybien.finance.Finance;
import pl.muybien.finance.FinanceDetail;
import pl.muybien.finance.FinanceRepository;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class DatabaseUpdater {

    private final FinanceRepository repository;

    @Transactional
    public void saveFinanceToDatabase(String assetType, Map<String, FinanceDetail> financeDetails) {
        String normalizedAssetType = assetType.toLowerCase();

        financeDetails.forEach((_, financeDetail) -> System.out.println(financeDetail));

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
