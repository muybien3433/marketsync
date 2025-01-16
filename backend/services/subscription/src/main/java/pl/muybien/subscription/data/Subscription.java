package pl.muybien.subscription.data;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Document(collection = "subscriptions")
@Getter
@Setter
public class Subscription {

    @Field("subscriptions")
    @Indexed(unique = true)
    private Map<String, List<SubscriptionDetail>> subscriptions = new HashMap<>();
}