package pl.muybien.marketsync.subscription;

import jakarta.persistence.*;
import pl.muybien.marketsync.customer.Customer;

@Entity
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Customer customer;
}
