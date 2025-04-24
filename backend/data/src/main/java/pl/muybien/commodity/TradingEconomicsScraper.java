package pl.muybien.commodity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.muybien.enums.AssetType;
import pl.muybien.enums.CurrencyType;
import pl.muybien.enums.UnitType;
import pl.muybien.finance.FinanceDetail;
import pl.muybien.updater.DatabaseUpdater;
import pl.muybien.updater.QueueUpdater;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class TradingEconomicsScraper extends QueueUpdater {

    private final DatabaseUpdater databaseUpdater;

    @Override
    @EventListener(ApplicationReadyEvent.class)
    @Scheduled(fixedDelay = 60000)
    public void scheduleUpdate() {
        enqueueUpdate("trading-economics");
    }

    @Override
    public void updateAssets() {
        log.info("Starting the update of TradingEconomics data...");

        Document doc;
        try {
            doc = Jsoup.connect("https://tradingeconomics.com/commodities").get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Elements rows = doc.select("table.table-hover tbody tr");

        var commodities = new HashMap<String, FinanceDetail>();

        for (Element row : rows) {
            Elements columns = row.select("td");
            if (columns.size() >= 5) {
                Element nameCell = columns.getFirst();
                String name = nameCell.text().trim();

                String unitText = "";
                Element unitElement = nameCell.selectFirst("small, div[style*=font-size: 10px]");
                if (unitElement != null) {
                    unitText = unitElement.text().trim();
                }

                String price = columns.get(1).text();

                CurrencyType currencyType = extractCurrency(unitText);
                UnitType unitType = extractUnit(unitText);
                String uri = name.trim().toLowerCase()
                        .replace(" ", "-")
                        .replaceAll("[^a-z0-9\\-]", "-"); // replaces slashes, dots, etc.

                FinanceDetail detail = new FinanceDetail(
                        name,
                        null,
                        uri,
                        unitType.name(),
                        price,
                        currencyType.name(),
                        AssetType.COMMODITY.name(),
                        LocalDateTime.now()
                );

                commodities.put(uri, detail);
            }
        }
        databaseUpdater.saveFinanceToDatabase(AssetType.COMMODITY.name(), commodities);
        log.info("Finished updating TradingEconomics data");
    }

    private CurrencyType extractCurrency(String unit) {
        if (unit.contains("USD")) return CurrencyType.USD;
        if (unit.contains("GBP")) return CurrencyType.GBP;
        if (unit.contains("EUR")) return CurrencyType.EUR;
        if (unit.contains("PLN")) return CurrencyType.PLN;
        return CurrencyType.USD; // fallback
    }

    private UnitType extractUnit(String unit) {
        for (UnitType type : UnitType.values()) {
            if (unit.toUpperCase().contains(type.name())) return type;
        }
        return UnitType.UNIT;
    }
}
