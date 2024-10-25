package pl.muybien.marketsync.subscription;

import jakarta.persistence.*;
import lombok.*;
import pl.muybien.marketsync.customer.Customer;

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
    private String customerEmail;

    @ManyToOne
    private Customer customer;
}
