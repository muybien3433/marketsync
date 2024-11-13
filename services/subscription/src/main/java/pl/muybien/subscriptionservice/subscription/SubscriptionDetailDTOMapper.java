package pl.muybien.subscriptionservice.subscription;

import org.springframework.stereotype.Component;

@Component
public class SubscriptionDetailDTOMapper {
    SubscriptionDetailDTO mapToDTO(SubscriptionDetail subscription) {
        return SubscriptionDetailDTO.builder()
                .financeName(subscription.getName())
                .upperBoundPrice(subscription.getUpperBoundPrice())
                .lowerBoundPrice(subscription.getLowerBoundPrice())
                .createdAt(subscription.getCreatedAt())
                .build();
    }
}
