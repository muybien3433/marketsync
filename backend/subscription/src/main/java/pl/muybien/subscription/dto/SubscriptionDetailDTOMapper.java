package pl.muybien.subscription.dto;

import org.springframework.stereotype.Service;
import pl.muybien.entity.helper.SubscriptionDetail;

@Service
public class SubscriptionDetailDTOMapper {
    public SubscriptionDetailDTO toDTO(SubscriptionDetail subscriptionDetail, String currentPrice) {
        return new SubscriptionDetailDTO(
                subscriptionDetail.id(),
                subscriptionDetail.financeName(),
                subscriptionDetail.uri(),
                currentPrice,
                subscriptionDetail.upperBoundPrice(),
                subscriptionDetail.lowerBoundPrice(),
                subscriptionDetail.assetType().name(),
                subscriptionDetail.notificationType().name(),
                subscriptionDetail.requestedCurrency().name(),
                subscriptionDetail.createdDate()
        );
    }
}