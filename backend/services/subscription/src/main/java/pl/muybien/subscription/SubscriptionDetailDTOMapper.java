package pl.muybien.subscription;

import org.springframework.stereotype.Component;

@Component
public class SubscriptionDetailDTOMapper {
    SubscriptionDetailDTO mapToDTO(SubscriptionDetail subscriptionDetail) {
        return SubscriptionDetailDTO.builder()
                .financeName(subscriptionDetail.getFinanceName())
                .upperBoundPrice(subscriptionDetail.getUpperBoundPrice())
                .lowerBoundPrice(subscriptionDetail.getLowerBoundPrice())
                .createdDate(subscriptionDetail.getCreatedDate())
                .build();
    }
}
