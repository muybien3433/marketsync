package pl.muybien.finance;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.muybien.enums.AssetType;

import java.time.LocalDateTime;
import java.util.*;

@Setter
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "finances")
public class Finance {

    @Id
    @EqualsAndHashCode.Include
    private String id;

    private Map<AssetType, Map<String, FinanceDetail>> financeDetails = new HashMap<>();

    public void initializeNestedMapIfNeeded(AssetType assetType) {
        this.financeDetails.computeIfAbsent(
                assetType, f -> new HashMap<>(22001, 0.75f)
        );
    }

    public List<FinanceDetail> cleanOldFinanceDetails() {
        LocalDateTime daysAgo = LocalDateTime.now().minusDays(1);
        List<FinanceDetail> removedDetails = new ArrayList<>();

        for (Map.Entry<AssetType, Map<String, FinanceDetail>> assetEntry : financeDetails.entrySet()) {
            Map<String, FinanceDetail> financeDetailMap = assetEntry.getValue();

            Iterator<Map.Entry<String, FinanceDetail>> iterator = financeDetailMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, FinanceDetail> entry = iterator.next();
                FinanceDetail detail = entry.getValue();

                if (!Objects.equals(detail.assetType(), AssetType.CUSTOM) && detail.lastUpdated().isBefore(daysAgo)) {
                    removedDetails.add(detail);
                    iterator.remove();
                }
            }
        }

        return removedDetails;
    }
}
