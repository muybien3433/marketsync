package pl.muybien.subscription.dto;

import org.springframework.stereotype.Service;
import pl.muybien.subscription.data.SubscriptionDetail;

@Service
public class SubscriptionDetailDTOMapper {
    public SubscriptionDetailDTO mapToDTO(SubscriptionDetail subscriptionDetail) {
        return SubscriptionDetailDTO.builder()
                .id(subscriptionDetail.getId())
                .customerId(subscriptionDetail.getCustomerId())
                .financeName(subscriptionDetail.getFinanceName())
                .upperBoundPrice(subscriptionDetail.getUpperBoundPrice())
                .lowerBoundPrice(subscriptionDetail.getLowerBoundPrice())
                .assetType(subscriptionDetail.getAssetType())
                .createdDate(subscriptionDetail.getCreatedDate())
                .build();
    }
}