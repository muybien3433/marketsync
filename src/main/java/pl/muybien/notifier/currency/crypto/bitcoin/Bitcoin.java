package pl.muybien.notifier.currency.crypto.bitcoin;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
@Table(name = "bitcoin")
public class Bitcoin {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long bitcoinId;
        private String symbol;
        private String name;
        private Double priceUsd;
}
