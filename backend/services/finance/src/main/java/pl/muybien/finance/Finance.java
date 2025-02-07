package pl.muybien.finance;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Document(collection = "finances")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@Getter
@Setter
public class Finance {

    @Id
    @EqualsAndHashCode.Include
    private String assetType;
    private Set<FinanceDetail> financeDetails;
}
