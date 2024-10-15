package pl.muybien.notifier.subscription;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pl.muybien.notifier.currency.crypto.bitcoin.Bitcoin;
import pl.muybien.notifier.customer.Customer;

@Entity
@Getter
@Setter
@Table(name = "subscription")
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToOne
    private Bitcoin bitcoin;
}
