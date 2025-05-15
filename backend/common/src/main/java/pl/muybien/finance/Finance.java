package pl.muybien.finance;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.muybien.enums.AssetType;

import java.time.LocalDateTime;
import java.util.*;

@Document(collection = "finances")
public class Finance {

    @Id
    private String id;
    private Map<String, Map<String, FinanceDetail>> financeDetails = new HashMap<>();

    public void initializeNestedMapIfNeeded(String normalizedAssetType) {
        this.financeDetails.computeIfAbsent(
                normalizedAssetType, f -> new HashMap<>(17001, 0.75f)
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

    public List<FinanceDetail> cleanOldFinanceDetails() {
        LocalDateTime daysAgo = LocalDateTime.now().minusDays(1);
        List<FinanceDetail> removedDetails = new ArrayList<>();

        for (Map.Entry<String, Map<String, FinanceDetail>> assetEntry : financeDetails.entrySet()) {
            Map<String, FinanceDetail> financeDetailMap = assetEntry.getValue();

            Iterator<Map.Entry<String, FinanceDetail>> iterator = financeDetailMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, FinanceDetail> entry = iterator.next();
                FinanceDetail detail = entry.getValue();

                if (!Objects.equals(detail.unitType(), AssetType.CUSTOM.name())
                        && detail.lastUpdated().isBefore(daysAgo)) {
                    removedDetails.add(detail);
                    iterator.remove();
                }
            }
        }

        return removedDetails;
    }
}
