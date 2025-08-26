package pl.muybien.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.muybien.entity.helper.SubscriptionDetail;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "subscription")
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Setter
public class Subscription {

    @Id
    @EqualsAndHashCode.Include
    private String uri;
    private List<SubscriptionDetail> subscriptionDetails = new ArrayList<>();
}