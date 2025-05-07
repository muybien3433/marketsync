package pl.muybien.currency;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.muybien.enums.UnitType;
import pl.muybien.enums.CurrencyType;
import pl.muybien.updater.QueueUpdater;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
@Slf4j
public class WiseScraper extends QueueUpdater {
    private static final String TARGET_URL = "https://wise.com/gb/currency-converter/";
    private static final String URL_SEPARATOR_ONE = "-to-";
    private static final String URL_SEPARATOR_TWO = "-rate?amount=1";
    private static final int RETRY_ATTEMPTS = 3;
    private static final int RETRY_DELAY_MS = 5000;

    private final CurrencyRepository repository;

    @Override
    @EventListener(ApplicationReadyEvent.class)
    @Scheduled(fixedDelay = 45000)
    public void scheduleUpdate() {
        enqueueUpdate("wise");
    }

    @Override
    public void updateAssets() {
        log.info("Starting the update of Wise data...");
        var allCurrencies = Arrays.asList(CurrencyType.values());

        for (int i = 0; i < allCurrencies.size(); i++) {
            for (int j = 0; j < allCurrencies.size(); j++) {
                if (i != j) {
                    CurrencyType from = allCurrencies.get(i);
                    CurrencyType to = allCurrencies.get(j);

                    calculateAndSaveNewExchangeRate(from, to);
                }
            }
        }
        log.info("Finished update Wise data");
    }

    private void calculateAndSaveNewExchangeRate(CurrencyType from, CurrencyType to) {
        BigDecimal exchangeRate = fetchExchangeRate(from, to);
        if (exchangeRate != null && exchangeRate.compareTo(BigDecimal.ZERO) > 0) {
            String name = currencyNameResolver(from, to);
            repository.findCurrencyByName(name)
                    .ifPresentOrElse(
                            currency -> {
                                currency.setExchange(exchangeRate);
                                repository.save(currency);
                            },
                            () -> repository.save(
                                    new Currency(
                                            name,
                                            exchangeRate,
                                            UnitType.UNIT.name(),
                                            LocalDateTime.now()
                                    )
                            )
                    );
        }
    }

    private BigDecimal fetchExchangeRate(CurrencyType from, CurrencyType to) {
        String url = TARGET_URL + from + URL_SEPARATOR_ONE + to + URL_SEPARATOR_TWO;

        for (int attempt = 1; attempt <= RETRY_ATTEMPTS; attempt++) {
            try {
                Document doc = Jsoup.connect(url).get();
                Element element = doc.select("span.text-success").first();
                if (element != null) {
                    String rateText = element.text().replace(",", "");
                    return new BigDecimal(rateText);
                }
                log.warn("No exchange rate found for {} to {} on attempt {}", from, to, attempt);
            } catch (IOException e) {
                log.warn("Failed to fetch exchange rate from {} to {} on attempt {}", from, to, attempt);
                if (attempt < RETRY_ATTEMPTS) {
                    try {
                        Thread.sleep(RETRY_DELAY_MS);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        log.error("Retry interrupted for {} to {}", from, to, ie);
                        break;
                    }
                }
            }
        }
        log.error("All attempts to fetch exchange rate from {} to {} have failed", from, to);
        return null;
    }

    private String currencyNameResolver(CurrencyType from, CurrencyType to) {
        return from.name().toLowerCase() + "-" + to.name().toLowerCase();
    }
}
