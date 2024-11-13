package pl.muybien.subscriptionservice.finance.crypto.binancecoin;

import jakarta.persistence.*;
import lombok.*;
import pl.muybien.subscriptionservice.finance.FinanceTarget;

import java.math.BigDecimal;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "binance-coin")
public class Binance implements FinanceTarget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private BigDecimal upperBoundPrice;
    private BigDecimal lowerBoundPrice;
    private String customerEmail;
}
