package pl.muybien.notifier.customer;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pl.muybien.notifier.subscription.Subscription;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "customer")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;

    @OneToMany(mappedBy = "customer")
    private List<Subscription> subscriptions;

}
