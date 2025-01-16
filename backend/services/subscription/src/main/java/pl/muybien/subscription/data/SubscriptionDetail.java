package pl.muybien.subscription.data;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.muybien.subscription.SubscriptionNotificationType;

import java.time.LocalDateTime;

@Document(collection = "subscriptions_detail")
@Builder
@Getter
@Setter
public class SubscriptionDetail {

    @Id
    private String id;
    private String uri;
    private String customerId;
    private String customerEmail;
    private String financeName;
    private Double upperBoundPrice;
    private Double lowerBoundPrice;
    private String assetType;
    private String notificationType;

    @CreatedDate
    private LocalDateTime createdDate;
    @LastModifiedDate
    private LocalDateTime lastModifiedDate;
}

