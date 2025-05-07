package pl.muybien.stock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.muybien.common.SeleniumHandler;
import pl.muybien.enums.AssetType;
import pl.muybien.enums.CurrencyType;
import pl.muybien.enums.UnitType;
import pl.muybien.finance.FinanceDetail;
import pl.muybien.finance.exception.FinanceNotFoundException;
import pl.muybien.updater.DatabaseUpdater;
import pl.muybien.updater.QueueUpdater;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class Gpw extends QueueUpdater {

    private final static String TARGET_URL = "https://www.gpw.pl/akcje";

    private final SeleniumHandler seleniumHandler;
    private final DatabaseUpdater databaseUpdater;

    @Override
    @EventListener(ApplicationReadyEvent.class)
    @Scheduled(fixedDelay = 300000)
    public void scheduleUpdate() {
        enqueueUpdate("gpw");
    }

    @Override
    @Transactional
    public void updateAssets() {
        log.info("Starting the update of Gpw data...");
        var stocks = new HashMap<String, FinanceDetail>();

        WebDriver driver = null;
        try {
            driver = seleniumHandler.getDriverAndNavigate(TARGET_URL);
            WebDriverWait wait = seleniumHandler.getDriverWait(driver, Duration.ofMillis(8000));
            seleniumHandler.handleCookieConsent(wait, By.xpath("//button[contains(text(),'Akceptuję')]"));

            WebElement body = driver.findElement(By.tagName("body"));
            String pageContent = body.getText();

            int indexOfFirst = pageContent.indexOf("AD");
            int indexOfLast = pageContent.lastIndexOf("Razem");
            if (indexOfFirst != -1 && indexOfLast != -1) {
                pageContent = pageContent.substring(indexOfFirst, indexOfLast);
            }

            String regex = ""
                    + "\\s+"                                // Empty field (not captured)
                    + "([A-Z]+)\\s+"                         // 1. Company name
                    + "([A-Z]+)\\s+"                         // 2. Symbol
                    + "([A-Z]+)\\s+"                         // 3. Currency
                    + "([0-9]{2}:[0-9]{2}:[0-9]{2})\\s+"     // 4. Time
                    + "([0-9,]+)\\s+"                        // 5. Reference price
                    + "([\\-]+)\\s+"                         // 6. Price change (can be "-" or numeric)
                    + "([0-9,]+)\\s+"                        // 7. Opening price
                    + "([0-9,]+)\\s+"                        // 8. Lowest price
                    + "([0-9,]+)\\s+"                        // 9. Highest price
                    + "([0-9,]+)\\s+"                        // 10. Current price
                    + "([\\-0-9,]+)\\s+"                     // 11. Percentage change (could be negative)
                    + "([0-9 ]+)\\s+"                        // 12. Volume
                    + "([0-9,]+)";                           // 13. Trading value

            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(pageContent);

            while (matcher.find()) {
                String name = matcher.group(1).trim();
                String uri = name.toLowerCase().replaceAll(" ", "-");
                String symbol = matcher.group(2).trim();
                String renewalRate = matcher.group(4).trim().replaceAll("\\s+", "").replace(",", ".");
                String lastTransactionRate = matcher.group(10).trim().replaceAll("\\s+", "").replace(",", ".");
                String price = lastTransactionRate.equals("-") ?
                        renewalRate : lastTransactionRate;

                var financeDetail = new FinanceDetail(
                        name,
                        symbol,
                        uri,
                        UnitType.UNIT.name(),
                        price,
                        CurrencyType.PLN.name(),
                        AssetType.STOCK.name(),
                        LocalDateTime.now()
                );
                stocks.put(uri, financeDetail);
            }
            databaseUpdater.saveFinanceToDatabase(AssetType.STOCK.name(), stocks);
        } catch (Exception e) {
            throw new FinanceNotFoundException("Gpw data: " + e.getMessage());
        } finally {
            if (driver != null) {
                driver.quit();
            }
            System.gc();
        }
        log.info("Found {} stocks", stocks.size());
        log.info("Finished updating Gpw data");
    }
}