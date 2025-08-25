package pl.muybien.updater;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.muybien.enums.AssetType;
import pl.muybien.finance.Finance;
import pl.muybien.finance.FinanceDetail;
import pl.muybien.finance.FinanceRepository;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class DatabaseUpdater {

    private final FinanceRepository repository;

    @Transactional
    public void saveFinanceToDatabase(AssetType assetType, Map<String, FinanceDetail> financeDetails) {
        repository.findFinanceByAssetType(assetType)
                .ifPresentOrElse(finance -> {
                            finance.initializeNestedMapIfNeeded(assetType);
                            finance.getFinanceDetails().get(assetType).putAll(financeDetails);
                            repository.save(finance);
                        },
                        () -> {
                            Finance newFinance = new Finance();
                            newFinance.initializeNestedMapIfNeeded(assetType);
                            newFinance.getFinanceDetails().get(assetType).putAll(financeDetails);
                            repository.save(newFinance);
                        }
                );
    }
}
