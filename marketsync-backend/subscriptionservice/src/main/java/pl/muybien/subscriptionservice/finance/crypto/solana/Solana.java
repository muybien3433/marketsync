package pl.muybien.subscriptionservice.finance.crypto.solana;

import jakarta.persistence.*;
import pl.muybien.marketsync.customer.Customer;
import pl.muybien.marketsync.finance.FinanceTarget;

import java.math.BigDecimal;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "solana")
public class Solana implements FinanceTarget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private BigDecimal upperBoundPrice;
    private BigDecimal lowerBoundPrice;

    @ManyToOne
    private Customer customer;
}