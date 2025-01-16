package pl.muybien.subscription.data;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "subscriptions_detail")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@Getter
@Setter
public class SubscriptionDetail {

    @Id
    @EqualsAndHashCode.Include
    private String id;
    private String uri;
    private String customerId;
    private String customerEmail;
    private String financeName;
    private Double requestedValue;
    private String requestedCurrency;
    private Double upperBoundPrice;
    private Double lowerBoundPrice;
    private String assetType;
    private String notificationType;

    @CreatedDate
    private LocalDateTime createdDate;
    @LastModifiedDate
    private LocalDateTime lastModifiedDate;
}

