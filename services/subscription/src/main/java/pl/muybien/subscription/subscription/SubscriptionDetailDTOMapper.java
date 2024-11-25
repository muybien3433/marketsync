package pl.muybien.subscription.subscription;

import org.springframework.stereotype.Component;

@Component
public class SubscriptionDetailDTOMapper {
    SubscriptionDetailDTO mapToDTO(SubscriptionDetail subscription) {
        return SubscriptionDetailDTO.builder()
                .financeName(subscription.getFinanceName())
                .upperBoundPrice(subscription.getUpperBoundPrice())
                .lowerBoundPrice(subscription.getLowerBoundPrice())
                .createdDate(subscription.getCreatedDate())
                .build();
    }
}
