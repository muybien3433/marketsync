package pl.muybien.finance.subscriptions.crypto.bitcoin;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import pl.muybien.finance.FinanceTarget;
import pl.muybien.subscription.Subscription;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "bitcoin")
public class Bitcoin implements FinanceTarget {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @EqualsAndHashCode.Include
        private Long id;
        private String financeName;
        private BigDecimal upperBoundPrice;
        private BigDecimal lowerBoundPrice;
        private String customerEmail;
        private String customerId;

        @CreatedDate
        @Column(updatable = false, nullable = false)
        private LocalDateTime createdDate;

        @LastModifiedDate
        @Column(insertable = false)
        private LocalDateTime lastModifiedDate;

        @OneToMany
        private List<Subscription> subscriptions;
}
