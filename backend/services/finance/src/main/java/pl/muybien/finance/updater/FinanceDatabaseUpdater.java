package pl.muybien.finance.updater;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.muybien.finance.Finance;
import pl.muybien.finance.FinanceDetail;
import pl.muybien.finance.FinanceRepository;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FinanceDatabaseUpdater {

    private final FinanceRepository repository;

    @Transactional
    public void sortAndSaveFinanceToDatabase(String assetType, LinkedHashSet<FinanceDetail> financeDetails) {
        var sortedFinances = financeDetails.stream()
                .sorted(Comparator.comparing(FinanceDetail::getName))
                .collect(Collectors.toCollection(LinkedHashSet::new));

        if (!sortedFinances.isEmpty()) {
            saveFinanceToDatabase(assetType, sortedFinances);
        }
    }

    private void saveFinanceToDatabase(String assetType, LinkedHashSet<FinanceDetail> sortedFinances) {
        repository.findFinanceByAssetTypeIgnoreCase(assetType.toLowerCase())
                .ifPresentOrElse(
                        existingFinance -> {
                            existingFinance.getFinanceDetails().addAll(sortedFinances);

                            repository.save(existingFinance);
                        },
                        () -> {
                            Finance newFinance = Finance.builder()
                                    .assetType(assetType.toLowerCase())
                                    .financeDetails(sortedFinances)
                                    .build();

                            repository.save(newFinance);
                        }
                );
    }
}
