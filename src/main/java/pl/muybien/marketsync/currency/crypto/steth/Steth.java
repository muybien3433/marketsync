package pl.muybien.marketsync.currency.crypto.steth;

import jakarta.persistence.*;
import lombok.*;
import pl.muybien.marketsync.currency.crypto.CryptoTarget;
import pl.muybien.marketsync.customer.Customer;

import java.math.BigDecimal;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "steth")
public class Steth implements CryptoTarget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private BigDecimal upperBoundPrice;
    private BigDecimal lowerBoundPrice;

    @ManyToOne
    private Customer customer;
}