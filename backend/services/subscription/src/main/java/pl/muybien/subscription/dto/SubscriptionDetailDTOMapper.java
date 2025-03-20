package pl.muybien.subscription.dto;

import org.springframework.stereotype.Service;
import pl.muybien.subscription.data.SubscriptionDetail;

@Service
public class SubscriptionDetailDTOMapper {
    public SubscriptionDetailDTO toDTO(SubscriptionDetail subscriptionDetail) {
        return new SubscriptionDetailDTO(
                subscriptionDetail.id(),
                subscriptionDetail.financeName(),
                subscriptionDetail.uri(),
                subscriptionDetail.upperBoundPrice(),
                subscriptionDetail.lowerBoundPrice(),
                subscriptionDetail.assetType().name(),
                subscriptionDetail.notificationType().name(),
                subscriptionDetail.createdDate()
        );
    }
}