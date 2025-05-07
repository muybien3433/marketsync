package pl.muybien.finance;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.muybien.enums.AssetType;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Document(collection = "finances")
public class Finance {

    @Id
    private String id;
    private Map<String, Map<String, FinanceDetail>> financeDetails = new HashMap<>();

    public void initializeNestedMapIfNeeded(String normalizedAssetType) {
        this.financeDetails.computeIfAbsent(
                normalizedAssetType, _ -> new HashMap<>(17001, 0.75f)
        );
    }

    public Finance() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, Map<String, FinanceDetail>> getFinanceDetails() {
        return financeDetails;
    }

    public void setFinanceDetails(Map<String, Map<String, FinanceDetail>> financeDetails) {
        this.financeDetails = financeDetails;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Finance finance = (Finance) o;
        return Objects.equals(id, finance.id) && Objects.equals(financeDetails, finance.financeDetails);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, financeDetails);
    }

    public void cleanOldFinanceDetails() {
        LocalDateTime daysAgo = LocalDateTime.now().minusDays(1);
        int removedCount = 0;

        for (Map.Entry<String, Map<String, FinanceDetail>> assetEntry : financeDetails.entrySet()) {
            Map<String, FinanceDetail> financeDetailMap = assetEntry.getValue();
            int initialSize = financeDetailMap.size();

            financeDetailMap.entrySet().removeIf(entry -> {
                FinanceDetail detail = entry.getValue();
                return !Objects.equals(detail.unitType(), AssetType.CUSTOM.name())
                        && detail.lastUpdated().isBefore(daysAgo);
            });

            removedCount += (initialSize - financeDetailMap.size());
        }
    }
}
