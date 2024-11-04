package pl.muybien.marketsync.wallet;

import jakarta.persistence.*;
import lombok.*;
import pl.muybien.marketsync.asset.Asset;
import pl.muybien.marketsync.customer.Customer;

import java.util.List;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "wallet")
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Customer customer;

    @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Asset> assets;

}
