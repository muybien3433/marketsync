package pl.muybien.notifier.currency.crypto.bitcoin;

import jakarta.persistence.*;
import lombok.*;
import pl.muybien.notifier.customer.Customer;

import java.math.BigDecimal;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bitcoin")
public class Bitcoin {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private String name;
        private BigDecimal priceUsd;
        private BigDecimal pointPrice;
        private BigDecimal upperBoundPrice;
        private BigDecimal lowerBoundPrice;

        @ManyToOne
        private Customer customer;
}
