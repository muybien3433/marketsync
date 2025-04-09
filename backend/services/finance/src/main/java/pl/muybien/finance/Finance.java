package pl.muybien.finance;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.Map;

@Document(collection = "finances")
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Finance {

    @Id
    private String id;
    private Map<String, Map<String, FinanceDetail>> financeDetails = new HashMap<>();

    public void initializeNestedMapIfNeeded(String normalizedAssetType) {
        this.financeDetails.computeIfAbsent(
                normalizedAssetType, _ -> new HashMap<>(17001, 0.75f)
        );
    }
}
