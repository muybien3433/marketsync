package pl.muybien.marketsync.currency.crypto.binancecoin;

import jakarta.persistence.*;
import lombok.*;
import pl.muybien.marketsync.currency.CurrencyTarget;
import pl.muybien.marketsync.customer.Customer;

import java.math.BigDecimal;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "binance-coin")
public class Binance implements CurrencyTarget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private BigDecimal upperBoundPrice;
    private BigDecimal lowerBoundPrice;

    @ManyToOne
    private Customer customer;
}
