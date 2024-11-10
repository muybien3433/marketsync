package pl.muybien.marketsync.customer;

import jakarta.persistence.*;
import lombok.*;
import pl.muybien.marketsync.wallet.Wallet;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "customer")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;

    @OneToOne(mappedBy = "customer", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Wallet wallet;
}
