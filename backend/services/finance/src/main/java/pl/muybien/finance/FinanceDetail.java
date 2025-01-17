package pl.muybien.finance;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "finances_detail")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@Getter
@Setter
public class FinanceDetail {

    @Id
    @EqualsAndHashCode.Include
    private String name;
    private String symbol;
    private String uri;
}
