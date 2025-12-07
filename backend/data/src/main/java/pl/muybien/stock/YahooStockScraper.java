package pl.muybien.stock;

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
public class YahooStockScraper extends YahooScraper {

    private static final List<String> STOCK_SCREENERS = List.of(
            "MOST_ACTIVES",
//            "TRENDING_NOW",
            "DAY_GAINERS",
            "DAY_LOSERS",
            "FIFTY_TWO_WK_GAINERS",
            "FIFTY_TWO_WK_LOSERS",
            "HIGHEST_DIVIDEND_STOCKS",
            "SMALL_CAP_STOCKS",
            "LARGE_CAP_STOCKS",
            "MOST_EXPENSIVE_STOCKS",
            "OVERSOLD_STOCKS",
            "PINK_SHEET_STOCKS",
            "OVERBOUGHT_STOCKS",
            "ALL_TIME_HIGH_STOCKS",
            "UNUSUAL_VOLUME_STOCKS",
            "HIGHEST_BETA_STOCKS",
            "HIGHEST_REVENUE_STOCKS",
            "HIGHEST_CASH_STOCKS",
            "HIGHEST_NET_INCOME_STOCKS",
            "HIGHEST_PROFIT_PER_EMPLOYEE_STOCKS",
            "HIGHEST_REVENUE_PER_EMPLOYEE_STOCKS",
            "LARGEST_EMPLOYER_STOCKS"
    );

    public YahooStockScraper(WebClient yahooWebClient, DatabaseUpdater databaseUpdater) {
        super(yahooWebClient, databaseUpdater);
    }

    @Override
    @EventListener(ApplicationReadyEvent.class)
    @Scheduled(fixedDelay = 60000)
    public void scheduleUpdate() {
        enqueueUpdate("yahoo-finance-stocks");
    }

    @Override
    protected String getScreenerId() {
        return "MOST_ACTIVES";
    }

    @Override
    protected List<String> getScreenerIds() {
        return STOCK_SCREENERS;
    }

    @Override
    protected AssetType getAssetType() {
        return AssetType.STOCK;
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
        return "ticker,symbol,longName,shortName,regularMarketPrice";
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
