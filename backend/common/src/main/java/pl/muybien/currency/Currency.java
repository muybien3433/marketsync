package pl.muybien.currency;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.muybien.enums.CurrencyType;
import pl.muybien.enums.UnitType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Document(collection = "currency_exchange")
public class Currency {

    @Id
    @EqualsAndHashCode.Include
    private CurrencyType name;

    private BigDecimal exchangeFromUSD;

    private UnitType unitType;

    private LocalDateTime lastModifiedDate;
}