package pl.muybien.finance.crypto.coinmarketcap;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.muybien.exception.FinanceNotFoundException;
import pl.muybien.finance.FinanceResponse;
import pl.muybien.finance.FinanceFileManager;
import pl.muybien.finance.FinanceFileDTO;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CoinmarketcapService {

    @Value("${coinmarketcap.file-name}")
    private String fileName;
    @Value("${coinmarketcap.base-url-currencies}")
    private String baseUrlCurrencies;
    @Value("${coinmarketcap.base-url-page}")
    private String baseUrlPage;
    @Value("${coinmarketcap.page-size}")
    private int pageSize;
    @Value("${coinmarketcap.jsoup-connect-timeout-in-ms}")
    private int jsoupConnectTimeoutInMs;

    @Value("${coinmarketcap.name-selector}")
    private String nameSelector;
    @Value("${coinmarketcap.name-attribute}")
    private String nameAttribute;
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

    private final FinanceFileManager financeFileManager;

    public FinanceResponse fetchCrypto(String uri) {
        try {
            if (uri == null || uri.isBlank()) {
                throw new IllegalArgumentException("Crypto identifier cannot be null or blank");
            }

            Document doc = Jsoup.connect(baseUrlCurrencies + uri).timeout(5000).get();
            String name = getElement(doc, nameSelector, nameAttribute);
            String price = getElement(doc, priceSelector);

            if (name != null && price != null) {
                BigDecimal priceAsBigDecimal = new BigDecimal(price.replaceAll("[$,]", ""));
                String currency = price.trim().substring(0, 1).replace("$", "USD");
                return FinanceResponse.builder()
                        .name(name)
                        .price(priceAsBigDecimal)
                        .currency(currency)
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

    private String getElement(Document doc, String selector) {
        Element element = doc.selectFirst(selector);
        return element != null ? element.text() : null;
    }

    private String getElement(Document doc, String selector, String attribute) {
        Element element = doc.selectFirst(selector);
        return element != null ? element.attr(attribute): null;
    }

    @PostConstruct
    private void runOnStartup() {
        updateAvailableCryptoList();
    }

    @PostConstruct
    @Scheduled(cron = "${coinmarketcap.update-schedule-cron}")
    private void updateAvailableCryptoList() {
        if (financeFileManager.isUpdateRequired(fileName)) {
            var cryptos = new LinkedHashSet<FinanceFileDTO>();
            int pageCounter = 1;
            while (pageCounter <= pageSize) {
                try {
                    Document doc = Jsoup.connect(baseUrlPage + pageCounter)
                            .timeout(jsoupConnectTimeoutInMs).get();

                    scrapDataFromFirstSection(doc, cryptos);
                    scrapDataFromSecondSection(doc, cryptos);
                    pageCounter++;
                } catch (IOException e) {
                    throw new FinanceNotFoundException("Error fetching finance data");
                }
            }
            sortAndSaveToFile(cryptos);
        }
    }

    private void scrapDataFromFirstSection(Document doc, LinkedHashSet<FinanceFileDTO> cryptos) {
        Elements links = doc.select(linkSelector);
        for (Element link : links) {
            String name = link.select(firstSectionNameSelector).text();
            String symbol = link.select(firstSectionSymbolSelector).text();
            String uri = link.attr(linkAttribute);

            if (!name.isBlank() && !symbol.isBlank() && !uri.isBlank()) {
                cryptos.add(new FinanceFileDTO(name, symbol, uri));
            }
        }
    }

    private void scrapDataFromSecondSection(Document doc, LinkedHashSet<FinanceFileDTO> cryptos) {
        Elements rows = doc.select(secondSectionRowsSelector);
        for (Element row : rows) {
            Element link = row.selectFirst(linkSelector);
            if (link != null) {
                String name = link.select(secondSectionNameSelector).get(secondSectionSpanNumber).text();
                String symbol = link.select(secondSectionSymbolSelector).text();
                String uri = link.attr(linkAttribute);

                if (!name.isBlank() && !symbol.isBlank() && !uri.isBlank()) {
                    cryptos.add(new FinanceFileDTO(name, symbol, uri));
                }
            }
        }
    }

    private void sortAndSaveToFile(LinkedHashSet<FinanceFileDTO> cryptos) {
        var sortedCryptos = cryptos.stream()
                .sorted(Comparator.comparing(FinanceFileDTO::getName))
                .collect(Collectors.toCollection(LinkedHashSet::new));

        financeFileManager.writeDataToFile(sortedCryptos, fileName);
    }
}