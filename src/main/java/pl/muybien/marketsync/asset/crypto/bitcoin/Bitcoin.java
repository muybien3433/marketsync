package pl.muybien.marketsync.asset.crypto.bitcoin;

import jakarta.persistence.*;
import lombok.*;
import pl.muybien.marketsync.asset.AssetTarget;
import pl.muybien.marketsync.customer.Customer;

import java.math.BigDecimal;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bitcoin")
public class Bitcoin implements AssetTarget {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private String name;
        private BigDecimal upperBoundPrice;
        private BigDecimal lowerBoundPrice;

        @ManyToOne
        private Customer customer;
}