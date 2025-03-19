package pl.muybien.subscription;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.muybien.finance.FinanceClient;
import pl.muybien.subscription.data.Subscription;
import pl.muybien.subscription.data.SubscriptionRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionScheduleChecker {

    @Value("${schedule.fetch-page-size-per-round}")
    private Integer fetchPagesSizePerRound;

    private final SubscriptionComparator subscriptionComparator;
    private final SubscriptionRepository subscriptionRepository;
    private final FinanceClient financeClient;

    @Scheduled(fixedRateString = "${schedule.time-ms}")
    public void compareSubscriptionsWithNewPrice() {
        Pageable pageable = PageRequest.of(0, fetchPagesSizePerRound);
        Page<Subscription> subscriptionPage;

        do {
            subscriptionPage = subscriptionRepository.findAllSubscriptions(pageable);
            subscriptionPage.forEach(this::processSubscriptions);

            pageable = pageable.next();
        } while (!subscriptionPage.isEmpty());
    }

    private void processSubscriptions(Subscription subscription) {
        String uri = subscription.getUri();
        var subscriptionDetails = subscription.getSubscriptionDetails();

        if (!subscriptionDetails.isEmpty()) {
            try {
                var finance = financeClient.findFinanceByAssetTypeAndUri(
                        subscriptionDetails.getFirst().assetType().name(), uri);

                double currentPrice = finance.price().doubleValue();

                subscriptionDetails.forEach(s -> {
                            try {
                                subscriptionComparator.priceMetSubscriptionConditionCheck(currentPrice, s);
                            } catch (Exception e) {
                                log.error("Error processing subscription detail for URI: {} and ID: {}", uri, s.id(), e);
                            }
                        }
                );
            } catch (Exception e) {
                log.error("Error processing subscription for URI: {}", uri, e);
            }
        }
    }
}

