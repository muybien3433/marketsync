package pl.muybien.etf;

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
public class YahooEtfScraper extends YahooScraper {

    private static final List<String> ETF_SCREENERS = List.of(
            "MOST_ACTIVES_ETFS",
            "DAY_GAINERS_ETFS",
            "DAY_LOSERS_ETFS",
            "FIFTY_TWO_WK_GAINERS_ETFS",
            "FIFTY_TWO_WK_LOSERS_ETFS",
            "TOP_PERFORMING_ETFS",
            "SP_500_ETFS",
            "CHEAPEST_ETFS"
    );

    public YahooEtfScraper(WebClient yahooWebClient, DatabaseUpdater databaseUpdater) {
        super(yahooWebClient, databaseUpdater);
    }

    @Override
    @EventListener(ApplicationReadyEvent.class)
    @Scheduled(fixedDelay = 20000)
    public void scheduleUpdate() {
        enqueueUpdate("yahoo-finance-etfs");
    }

    @Override
    protected String getScreenerId() {
        return "MOST_ACTIVES_ETFS";
    }

    @Override
    protected List<String> getScreenerIds() {
        return ETF_SCREENERS;
    }

    @Override
    protected AssetType getAssetType() {
        return AssetType.ETF;
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
