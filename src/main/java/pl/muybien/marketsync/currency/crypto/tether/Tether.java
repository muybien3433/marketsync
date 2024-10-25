package pl.muybien.marketsync.currency.crypto.tether;

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
@Table(name = "tether")
public class Tether implements CurrencyTarget {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private String name;
        private BigDecimal upperBoundPrice;
        private BigDecimal lowerBoundPrice;

        @ManyToOne
        private Customer customer;
}
