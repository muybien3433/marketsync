package pl.muybien.notifier.currency.crypto.bitcoin;

import jakarta.persistence.*;
import lombok.*;
import pl.muybien.notifier.currency.crypto.CryptoTarget;
import pl.muybien.notifier.customer.Customer;

import java.math.BigDecimal;

@Entity
@Builder
@Getter
@Setter
@RequiredArgsConstructor
@Table(name = "bitcoin")
public class Bitcoin implements CryptoTarget {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private String name;
        private BigDecimal pointPrice;
        private BigDecimal upperBoundPrice;
        private BigDecimal lowerBoundPrice;

        @ManyToOne
        private Customer customer;
}
