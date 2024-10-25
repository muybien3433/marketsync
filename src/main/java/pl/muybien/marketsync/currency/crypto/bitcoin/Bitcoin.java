package pl.muybien.marketsync.currency.crypto.bitcoin;

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
@Table(name = "bitcoin")
public class Bitcoin implements CurrencyTarget {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private String name;
        private BigDecimal upperBoundPrice;
        private BigDecimal lowerBoundPrice;

        @ManyToOne
        private Customer customer;
}
