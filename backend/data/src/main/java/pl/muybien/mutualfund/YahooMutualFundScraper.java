package pl.muybien.mutualfund;

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

import java.util.List;

@Service
@Transactional
@Slf4j
public class YahooMutualFundScraper extends YahooScraper {

    private static final List<String> MUTUAL_FUNDS_SCREENERS = List.of(
            "DAY_GAINERS_MUTUAL_FUNDS",
            "DAY_LOSERS_MUTUAL_FUNDS",
            "TOP_PERFORMING_MUTUAL_FUNDS",
            "TOP_MUTUAL_FUNDS",
            "BEST_HIST_PERFORMANCE_MUTUAL_FUNDS"
    );

    public YahooMutualFundScraper(WebClient yahooWebClient, DatabaseUpdater databaseUpdater) {
        super(yahooWebClient, databaseUpdater);
    }

    @Override
    @EventListener(ApplicationReadyEvent.class)
    @Scheduled(fixedDelay = 60000)
    public void scheduleUpdate() {
        enqueueUpdate("yahoo-finance-mutual-funds");
    }

    @Override
    protected String getScreenerId() {
        return "BEST_HIST_PERFORMANCE_MUTUAL_FUNDS";
    }

    @Override
    protected List<String> getScreenerIds() {
        return MUTUAL_FUNDS_SCREENERS;
    }

    @Override
    protected AssetType getAssetType() {
        return AssetType.MUTUAL_FUNDS;
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
    protected String getFields() {
        return "symbol,shortName,longName,regularMarketPrice";
    }

    @Override
    protected String normalizeSymbol(String ticker) {
        if (ticker == null) {
            return "";
        }
        return ticker.trim();
    }

    @Override
    protected String normalizeName(String companyName) {
        if (companyName == null) {
            return "";
        }
        return companyName.trim();
    }
}
