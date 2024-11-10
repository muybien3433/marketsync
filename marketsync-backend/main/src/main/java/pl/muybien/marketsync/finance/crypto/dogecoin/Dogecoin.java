package pl.muybien.marketsync.finance.crypto.dogecoin;

import jakarta.persistence.*;
import lombok.*;
import pl.muybien.marketsync.finance.FinanceTarget;
import pl.muybien.marketsync.customer.Customer;

import java.math.BigDecimal;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "dogecoin")
public class Dogecoin implements FinanceTarget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private BigDecimal upperBoundPrice;
    private BigDecimal lowerBoundPrice;

    @ManyToOne
    private Customer customer;
}
