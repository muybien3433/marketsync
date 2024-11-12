package pl.muybien.subscriptionservice.subscription;

import org.springframework.stereotype.Component;

@Component
public class SubscriptionDTOMapper {
    SubscriptionDTO mapToDTO(SubscriptionDetail subscription) {
        return SubscriptionDTO.builder()
                .financeName(subscription.getName())
                .upperBoundPrice(subscription.getUpperBoundPrice())
                .lowerBoundPrice(subscription.getLowerBoundPrice())
                .createdAt(subscription.getCreatedAt())
                .build();
    }
}
