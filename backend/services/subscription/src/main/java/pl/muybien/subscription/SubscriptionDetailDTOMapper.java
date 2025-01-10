package pl.muybien.subscription;

import org.springframework.stereotype.Service;

@Service
public class SubscriptionDetailDTOMapper {
    public SubscriptionDetailDTO mapToDTO(SubscriptionDetail subscriptionDetail) {
        return SubscriptionDetailDTO.builder()
                .customerId(subscriptionDetail.getCustomerId())
                .financeName(subscriptionDetail.getFinanceName())
                .upperBoundPrice(subscriptionDetail.getUpperBoundPrice())
                .lowerBoundPrice(subscriptionDetail.getLowerBoundPrice())
                .assetType(subscriptionDetail.getAssetType())
                .createdDate(subscriptionDetail.getCreatedDate())
                .build();
    }
}