package pl.muybien.marketsync.subscription;

import jakarta.persistence.*;
import lombok.*;
import pl.muybien.marketsync.customer.Customer;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "subscription")
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long stockId;
    private String stockName;
    private BigDecimal upperBoundPrice;
    private BigDecimal lowerBoundPrice;
    private String customerEmail;
    private LocalDateTime createdAt;

    @ManyToOne
    private Customer customer;
}
