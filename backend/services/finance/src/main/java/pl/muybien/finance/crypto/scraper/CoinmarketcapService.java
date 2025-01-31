package pl.muybien.finance.crypto.scraper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.muybien.exception.FinanceNotFoundException;
import pl.muybien.finance.FinanceDetail;
import pl.muybien.finance.FinanceResponse;
import pl.muybien.finance.FinanceUpdater;
import pl.muybien.finance.crypto.CryptoService;
import pl.muybien.finance.currency.CurrencyService;
import pl.muybien.finance.currency.CurrencyType;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CoinmarketcapService implements CryptoService {

    @Value("${coinmarketcap.type}")
    private String assetType;
    @Value("${coinmarketcap.base-url-currencies}")
    private String baseUrlCurrencies;
    @Value("${coinmarketcap.base-url-page}")
    private String baseUrlPage;
    @Value("${coinmarketcap.page-size}")
    private int pageSize;
    @Value("${coinmarketcap.jsoup-update-connect-timeout-in-ms}")
    private int jsoupConnectUpdateTimeoutInMs;
    @Value("${coinmarketcap.jsoup-fetch-connect-timeout-in-ms}")
    private int jsoupConnectFetchTimeoutInMs;
    @Value("${coinmarketcap.name-selector}")
    private String nameSelector;
    @Value("${coinmarketcap.name-attribute}")
    private String nameAttribute;
    @Value("${coinmarketcap.symbol-selector}")
    private String symbolSelector;
    @Value("${coinmarketcap.price-selector}")
    private String priceSelector;
    @Value("${coinmarketcap.link-selector}")
    private String linkSelector;
    @Value("${coinmarketcap.link-attribute}")
    private String linkAttribute;

    @Value("${coinmarketcap.first-section-name-selector}")
    private String firstSectionNameSelector;
    @Value("${coinmarketcap.first-section-symbol-selector}")
    private String firstSectionSymbolSelector;

    @Value("${coinmarketcap.second-section-rows-selector}")
    private String secondSectionRowsSelector;
    @Value("${coinmarketcap.second-section-name-selector}")
    private String secondSectionNameSelector;
    @Value("${coinmarketcap.second-section-span-number}")
    private int secondSectionSpanNumber;
    @Value("${coinmarketcap.second-section-symbol-selector}")
    private String secondSectionSymbolSelector;

    private final CurrencyService currencyService;
    private final FinanceUpdater financeUpdater;

    @Async
    @EventListener(ApplicationReadyEvent.class)
    @Scheduled(cron = "${coinmarketcap.update-schedule-cron}")
    @Override
    public void updateAvailableFinanceList() {
        var cryptos = new LinkedHashSet<FinanceDetail>();
        int pageCounter = 1;
        log.info("Starting updating available cryptos list...");
        while (pageCounter <= pageSize) {
            try {
                Document doc = Jsoup.connect(baseUrlPage + pageCounter)
                        .timeout(jsoupConnectUpdateTimeoutInMs).get();

                scrapDataFromFirstSection(doc, cryptos);
                scrapDataFromSecondSection(doc, cryptos);
                pageCounter++;
            } catch (IOException e) {
                throw new FinanceNotFoundException("Error fetching finance data");
            }
        }
        log.info("Finished updating available cryptos list");
        financeUpdater.sortAndSaveFinanceToDatabase(assetType, cryptos);
    }

    @Override
    public FinanceResponse fetchCrypto(String uri, String assetType, String currency) {
        try {
            if (uri == null || uri.isBlank()) {
                throw new IllegalArgumentException("Crypto identifier cannot be null or blank");
            }

            Document doc = Jsoup.connect(baseUrlCurrencies + uri).timeout(jsoupConnectFetchTimeoutInMs).get();
            String name = getElement(doc, nameSelector, nameAttribute);
            String price = getElement(doc, priceSelector);
            String symbol = getElement(doc, symbolSelector);

            if (name != null && price != null) {
                BigDecimal priceAsBigDecimal = new BigDecimal(price.replaceAll("[$,]", ""));
                String baseCurrency = "USD"; // USD is default in this website

                if (currency != null && !baseCurrency.equals(currency.toUpperCase())) {
                    BigDecimal exchangeRate = currencyService.getCurrencyPairExchange(
                            CurrencyType.fromString(baseCurrency), CurrencyType.fromString(currency));
                    priceAsBigDecimal = priceAsBigDecimal.multiply(exchangeRate);
                } else {
                    currency = baseCurrency;
                }

                return FinanceResponse.builder()
                        .name(name)
                        .symbol(symbol)
                        .price(priceAsBigDecimal)
                        .currency(CurrencyType.fromString(currency))
                        .assetType(assetType.toLowerCase())
                        .build();
            } else {
                log.warn("Missing data: name={}, price={}", name, price);
                throw new FinanceNotFoundException("Name or/and price not found");
            }
        } catch (IOException e) {
            log.error("Error fetching data for crypto: {}", uri, e);
            throw new FinanceNotFoundException("Error fetching finance data");
        }
    }

    @Override
    public FinanceResponse fetchCrypto(String uri, String assetType) {
        return fetchCrypto(uri, assetType, null);
    }

    private String getElement(Document doc, String selector) {
        Element element = doc.selectFirst(selector);
        return element != null ? element.text() : null;
    }

    private String getElement(Document doc, String selector, String attribute) {
        Element element = doc.selectFirst(selector);
        return element != null ? element.attr(attribute): null;
    }

    private void scrapDataFromFirstSection(Document doc, LinkedHashSet<FinanceDetail> cryptos) {
        Elements links = doc.select(linkSelector);
        for (Element link : links) {
            String name = link.select(firstSectionNameSelector).text();
            String symbol = link.select(firstSectionSymbolSelector).text();
            String uri = extractUri(link.attr(linkAttribute));

            if (!name.isBlank() && !symbol.isBlank() && !uri.isBlank()) {
                var financeDetail = FinanceDetail.builder()
                        .name(name)
                        .symbol(symbol)
                        .uri(uri)
                        .build();
                cryptos.add(financeDetail);
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

    private void scrapDataFromSecondSection(Document doc, LinkedHashSet<FinanceDetail> cryptos) {
        Elements rows = doc.select(secondSectionRowsSelector);
        for (Element row : rows) {
            Element link = row.selectFirst(linkSelector);
            if (link != null) {
                String name = link.select(secondSectionNameSelector).get(secondSectionSpanNumber).text();
                String symbol = link.select(secondSectionSymbolSelector).text();
                String uri = extractUri(link.attr(linkAttribute));

                if (!name.isBlank() && !symbol.isBlank() && !uri.isBlank()) {
                    var financeDetail = FinanceDetail.builder()
                            .name(name)
                            .symbol(symbol)
                            .uri(uri)
                            .build();
                    cryptos.add(financeDetail);
                }
            }
        }
    }
}