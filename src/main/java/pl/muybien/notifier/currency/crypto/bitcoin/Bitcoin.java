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
        private Long btcId;
        private String id;
        private String rank;
        private String symbol;
        private String name;
        private Double priceUsd;

        @Override
        public String toString() {
                return "Bitcoin{id='%s', rank='%s', symbol='%s', name='%s', priceUsd=%s}"
                        .formatted(id, rank, symbol, name, priceUsd);
        }
}
