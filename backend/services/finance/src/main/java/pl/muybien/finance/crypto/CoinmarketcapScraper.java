package pl.muybien.finance.crypto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.muybien.exception.FinanceNotFoundException;
import pl.muybien.finance.*;
import pl.muybien.finance.updater.FinanceDatabaseUpdater;
import pl.muybien.finance.updater.FinanceUpdater;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class CoinmarketcapScraper extends FinanceUpdater {

    @Value("${coinmarketcap.base-url-page}")
    private String baseUrlPage;
    @Value("${coinmarketcap.page-size}")
    private int pageSize;
    @Value("${coinmarketcap.jsoup-update-connect-timeout-in-ms}")
    private int jsoupConnectUpdateTimeoutInMs;
    @Value("${coinmarketcap.link-selector}")
    private String linkSelector;
    @Value("${coinmarketcap.link-attribute}")
    private String linkAttribute;

    @Value("${coinmarketcap.first-section-name-selector}")
    private String firstSectionNameSelector;
    @Value("${coinmarketcap.first-section-symbol-selector}")
    private String firstSectionSymbolSelector;
    @Value("${coinmarketcap.first-section-price-selector}")
    private String firstSectionPriceSelector;
    @Value("${coinmarketcap.second-section-rows-selector}")

    private String secondSectionRowsSelector;
    @Value("${coinmarketcap.second-section-name-selector}")
    private String secondSectionNameSelector;
    @Value("${coinmarketcap.second-section-span-number}")
    private int secondSectionSpanNumber;
    @Value("${coinmarketcap.second-section-symbol-selector}")
    private String secondSectionSymbolSelector;
    @Value("${coinmarketcap.second-section-price-selector}")
    private String secondSectionPriceSelector;

    private final FinanceDatabaseUpdater databaseUpdater;

    @Override
    @EventListener(ApplicationReadyEvent.class)
    @Scheduled(fixedRateString = "${coinmarketcap.crypto-updater-frequency-ms}")
    public void scheduleUpdate() {
        enqueueUpdate("coinmarketcap");
    }

    @Override
    @Transactional
    public void updateAssets() {
        log.info("Starting updating crypto data...");
        var cryptos = new HashMap<String, FinanceDetail>();
        int pageCounter = 1;
        while (pageCounter++ <= pageSize) {
            try {
                Document doc = Jsoup.connect(baseUrlPage + pageCounter)
                        .timeout(jsoupConnectUpdateTimeoutInMs).get();
                
                scrapDataFromFirstSection(doc, cryptos);
                scrapDataFromSecondSection(doc, cryptos);
                TimeUnit.MILLISECONDS.sleep(300);

                databaseUpdater.saveFinanceToDatabase(AssetType.CRYPTOS.name(), cryptos);
            } catch (IOException e) {
                throw new FinanceNotFoundException("Error fetching finance data");
            } catch (Exception e) {
                throw new FinanceNotFoundException("Coinmarketcap data: " + e.getMessage());
            }
        }
        log.info("Finished updating crypto data");
    }

    private void scrapDataFromFirstSection(Document doc, Map<String, FinanceDetail> cryptos) {
        Elements links = doc.select(linkSelector);

        for (Element link : links) {
            String name = link.select(firstSectionNameSelector).text();
            String symbol = link.select(firstSectionSymbolSelector).text();
            String uri = extractUri(link.attr(linkAttribute).toLowerCase());
            BigDecimal price = null;
            Element row = link.closest("tr");
            if (row != null) {
                Element priceSpan = row.selectFirst(firstSectionPriceSelector);
                if (priceSpan != null) {
                    price = parsePrice(priceSpan.text());
                }
            }

            if (!name.isBlank() && !symbol.isBlank() && !uri.isBlank() && price != null) {
                var financeDetail = new FinanceDetail(
                        name,
                        symbol,
                        uri,
                        price,
                        CurrencyType.USD.name(),
                        AssetType.CRYPTOS.name(),
                        LocalTime.now()
                );
                cryptos.put(uri, financeDetail);
            }
        }
    }

    private void scrapDataFromSecondSection(Document doc, Map<String, FinanceDetail> cryptos) {
        Elements rows = doc.select(secondSectionRowsSelector);
        for (Element row : rows) {
            Element link = row.selectFirst(linkSelector);
            if (link != null) {
                String name = link.select(secondSectionNameSelector).get(secondSectionSpanNumber).text();
                String symbol = link.select(secondSectionSymbolSelector).text();
                String uri = extractUri(link.attr(linkAttribute));

                Element priceElement = row.select(secondSectionPriceSelector).get(3);
                BigDecimal price = extractPrice(priceElement.text());

                if (!name.isBlank() && !symbol.isBlank() && !uri.isBlank()) {
                    var financeDetail = new FinanceDetail(
                            name,
                            symbol,
                            uri,
                            price,
                            CurrencyType.USD.name(),
                            AssetType.CRYPTOS.name(),
                            LocalTime.now()
                    );
                    cryptos.put(uri, financeDetail);
                }
            }
        }
    }

    private String extractUri(String uri) {
        if (uri != null && uri.startsWith("/currencies")) {
            return uri.substring("/currencies/".length(),
                    uri.endsWith("/") ? uri.length() - 1 : uri.length());
        }
        return "";
    }

    private BigDecimal parsePrice(String priceText) {
        if (priceText == null || priceText.isBlank()) return null;

        try {
            String numericText = priceText.replaceAll("[^\\d.]", "");
            return new BigDecimal(numericText);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private BigDecimal extractPrice(String priceText) {
        String cleanPrice = priceText.replace("$", "").replace(",", "").trim();
        return new BigDecimal(cleanPrice);
    }
}