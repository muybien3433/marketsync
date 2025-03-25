package pl.muybien.subscription.dto;

import org.springframework.stereotype.Service;
import pl.muybien.subscription.data.SubscriptionDetail;

import java.math.BigDecimal;

@Service
public class SubscriptionDetailDTOMapper {
    public SubscriptionDetailDTO toDTO(SubscriptionDetail subscriptionDetail, BigDecimal currentPrice) {
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