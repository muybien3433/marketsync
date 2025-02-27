package pl.muybien.subscription.data;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Document(collection = "subscriptions")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Setter
public class Subscription {

    @Id
    @EqualsAndHashCode.Include
    private String id;
    private Map<String, List<SubscriptionDetail>> subscriptions = new HashMap<>();
}