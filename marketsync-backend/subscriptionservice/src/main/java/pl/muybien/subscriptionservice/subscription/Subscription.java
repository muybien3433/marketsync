package pl.muybien.subscriptionservice.subscription;

import jakarta.persistence.*;
import lombok.*;

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
    private Long financeId;
    private String name;
    private BigDecimal upperBoundPrice;
    private BigDecimal lowerBoundPrice;
    private String customerEmail;
    private LocalDateTime createdAt;
}
