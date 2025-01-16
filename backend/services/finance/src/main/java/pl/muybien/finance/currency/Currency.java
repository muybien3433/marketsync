package pl.muybien.finance.currency;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Document(collection = "currency_exchange")
@Builder
@Getter
@Setter
public class Currency {

    @Id
    private String name;
    private BigDecimal exchange;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;
}