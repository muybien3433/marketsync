package pl.muybien.marketsync.wallet;

import jakarta.persistence.*;
import lombok.*;
import pl.muybien.marketsync.customer.Customer;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
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
    @GeneratedValue
    private Long id;

    private String financeName;
    private BigDecimal count;
    private BigDecimal currentPrice;
    private Integer investmentPeriod;
    private Double profitInPercentage;
    private BigDecimal profit;
    private String customerEmail;
    private LocalDateTime creationDate;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<BigInteger> averagePurchasePrice;

    @OneToOne
    private Customer customer;

}
