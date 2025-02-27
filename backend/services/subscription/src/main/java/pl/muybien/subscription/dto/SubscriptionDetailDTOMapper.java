package pl.muybien.subscription.dto;

import org.springframework.stereotype.Service;
import pl.muybien.subscription.data.SubscriptionDetail;

@Service
public class SubscriptionDetailDTOMapper {
    public SubscriptionDetailDTO toDTO(SubscriptionDetail subscriptionDetail) {
        return new SubscriptionDetailDTO(
                subscriptionDetail.id(),
                subscriptionDetail.customerId(),
                subscriptionDetail.financeName(),
                subscriptionDetail.upperBoundPrice(),
                subscriptionDetail.lowerBoundPrice(),
                subscriptionDetail.assetType(),
                subscriptionDetail.createdDate()
        );
    }
}