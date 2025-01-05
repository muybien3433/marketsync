    package pl.muybien.subscription;

    import jakarta.persistence.*;
    import lombok.*;
    import org.springframework.data.annotation.CreatedDate;
    import org.springframework.data.annotation.LastModifiedDate;
    import org.springframework.data.jpa.domain.support.AuditingEntityListener;

    import java.math.BigDecimal;
    import java.time.LocalDateTime;

    @Entity
    @Builder
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @EntityListeners(AuditingEntityListener.class)
    @Table(name = "subscription_detail")
    public class SubscriptionDetail {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private String customerId;

        private String financeName;
        private BigDecimal upperBoundPrice;
        private BigDecimal lowerBoundPrice;

        @CreatedDate
        @Column(updatable = false, nullable = false)
        private LocalDateTime createdDate;

        @LastModifiedDate
        @Column(insertable = false)
        private LocalDateTime lastModifiedDate;

        @ManyToOne(fetch = FetchType.EAGER)
        private Subscription subscription;
    }
