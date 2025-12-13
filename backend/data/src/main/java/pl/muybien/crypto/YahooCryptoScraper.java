package pl.muybien.crypto;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import pl.muybien.common.YahooScraper;
import pl.muybien.enumeration.AssetType;
import pl.muybien.enumeration.CurrencyType;
import pl.muybien.enumeration.UnitType;
import pl.muybien.updater.DatabaseUpdater;

@Service
@Transactional
@Slf4j
public class YahooCryptoScraper extends YahooScraper {

    private static final String SCREENER_ID = "ALL_CRYPTOCURRENCIES_US";

    public YahooCryptoScraper(WebClient yahooWebClient, DatabaseUpdater databaseUpdater) {
        super(yahooWebClient, databaseUpdater);
    }

    @Override
    @EventListener(ApplicationReadyEvent.class)
    @Scheduled(fixedDelay = 10000)
    public void scheduleUpdate() {
        enqueueUpdate("yahoo-finance-crypto");
    }

    @Override
    protected String getScreenerId() {
        return SCREENER_ID;
    }

    @Override
    protected AssetType getAssetType() {
        return AssetType.CRYPTO;
    }

    @Override
    protected CurrencyType getCurrencyType() {
        return CurrencyType.USD;
    }

    @Override
    protected UnitType getUnitType() {
        return UnitType.UNIT;
    }

    @Override
    protected String normalizeSymbol(String ticker) {
        if (ticker != null && ticker.endsWith("-USD") && ticker.length() > 4) {
            return ticker.substring(0, ticker.length() - 4);
        }
        return ticker;
    }

    @Override
    protected String normalizeName(String companyName) {
        if (companyName != null && companyName.endsWith(" USD") && companyName.length() > 4) {
            return companyName.substring(0, companyName.length() - 4);
        }
        return companyName;
    }
}
