package pl.muybien.subscription;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.muybien.feign.FinanceClient;
import pl.muybien.response.FinanceResponse;
import pl.muybien.kafka.confirmation.SubscriptionConfirmation;
import pl.muybien.kafka.SubscriptionProducer;
import pl.muybien.entity.helper.SubscriptionDetail;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionComparator {

    private final SubscriptionProducer subscriptionProducer;
    private final SubscriptionService subscriptionService;
    private final FinanceClient financeClient;

    @Transactional
    public void priceMetSubscriptionConditionCheck(FinanceResponse finance, SubscriptionDetail subscriptionDetail) {
        if (subscriptionDetail != null) {
            Double upperTargetPrice = subscriptionDetail.upperBoundPrice();
            Double lowerTargetPrice = subscriptionDetail.lowerBoundPrice();

            boolean financeCurrencyDifferentThanSubscription =
                    !finance.currencyType().equals(subscriptionDetail.requestedCurrency());

            BigDecimal currentPrice = new BigDecimal(finance.price());
            if (financeCurrencyDifferentThanSubscription) {
                var rate = financeClient
                        .findExchangeRate(finance.currencyType(), subscriptionDetail.requestedCurrency());

                currentPrice = currentPrice.multiply(rate);
            }

            if (upperTargetPrice != null) {
                if (currentPrice.compareTo(new BigDecimal(upperTargetPrice)) > 0) {
                    sendNotificationToSpecifiedTopic(subscriptionDetail, currentPrice, upperTargetPrice);
                }
            }

            if (lowerTargetPrice != null) {
                if (currentPrice.compareTo(new BigDecimal(lowerTargetPrice)) < 0) {
                    sendNotificationToSpecifiedTopic(subscriptionDetail, currentPrice, lowerTargetPrice);
                }
            }
        } else {
            log.error("Subscription detail is null");
        }
    }

    @Transactional
    public void sendNotificationToSpecifiedTopic(SubscriptionDetail detail, BigDecimal price, Double targetPrice) {
        String message = "Current %s value reached bound at: %s, your bound was %s"
                .formatted(detail.financeName(), price.toPlainString(), targetPrice);
        var subscriptionConfirmation = new SubscriptionConfirmation(
                detail.notificationType(),
                detail.target(),
                message
        );
        subscriptionProducer.sendSubscriptionNotification(subscriptionConfirmation);
        subscriptionService.deleteSubscription(detail.customerId(), detail.uri(), detail.id());
    }
}
