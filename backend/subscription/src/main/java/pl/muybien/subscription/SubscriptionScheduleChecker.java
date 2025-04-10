package pl.muybien.subscription;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    @Transactional
    public void processSubscriptions(Subscription subscription) {
        String uri = subscription.getUri();
        var subscriptionDetails = subscription.getSubscriptionDetails();

        if (!subscriptionDetails.isEmpty()) {
            var finance = financeClient.findFinanceByTypeAndUri(
                    subscriptionDetails.getFirst().assetType().name(), uri);

            subscriptionDetails.forEach(s ->
                    subscriptionComparator.priceMetSubscriptionConditionCheck(finance, s)
            );
        }
    }
}

