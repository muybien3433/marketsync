package pl.muybien.subscription.dto;

import org.springframework.stereotype.Service;
import pl.muybien.subscription.data.SubscriptionDetail;

@Service
public class SubscriptionDetailDTOMapper {
    public SubscriptionDetailDTO toDTO(SubscriptionDetail subscriptionDetail) {
        return new SubscriptionDetailDTO(
                subscriptionDetail.getId(),
                subscriptionDetail.getCustomerId(),
                subscriptionDetail.getFinanceName(),
                subscriptionDetail.getUpperBoundPrice(),
                subscriptionDetail.getLowerBoundPrice(),
                subscriptionDetail.getAssetType(),
                subscriptionDetail.getCreatedDate()
        );
    }
}