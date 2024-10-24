package pl.muybien.notifier.currency.crypto.tron;

import jakarta.persistence.*;
import lombok.*;
import pl.muybien.notifier.currency.crypto.CryptoTarget;
import pl.muybien.notifier.customer.Customer;

import java.math.BigDecimal;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tron")
public class Tron implements CryptoTarget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private BigDecimal upperBoundPrice;
    private BigDecimal lowerBoundPrice;

    @ManyToOne
    private Customer customer;
}
