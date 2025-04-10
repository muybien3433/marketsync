package pl.muybien.finance.stock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.muybien.enums.AssetType;
import pl.muybien.enums.CurrencyType;
import pl.muybien.enums.UnitType;
import pl.muybien.exception.FinanceNotFoundException;
import pl.muybien.finance.*;
import pl.muybien.finance.updater.FinanceDatabaseUpdater;
import pl.muybien.finance.updater.FinanceUpdater;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class Gpw extends FinanceUpdater {

    private final static String TARGET_URL = "https://www.gpw.pl/akcje";
    private final static String REMOTE_WEB_DRIVER = "http://selenium-chrome:4444/wd/hub";

    private final FinanceDatabaseUpdater databaseUpdater;

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
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--headless=new");
            options.addArguments("--disable-blink-features=AutomationControlled");
            options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
            options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Safari/537.36");
            options.addArguments("--disable-extensions");
            options.addArguments("--disable-gpu");
            options.addArguments("--disable-logging");

            driver = new RemoteWebDriver(new URL(REMOTE_WEB_DRIVER), options);
            driver.get(TARGET_URL);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(8));

            try {
                WebElement acceptCookiesButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(),'Akceptuję')]")));
                acceptCookiesButton.click();
                log.info("Accepted cookie policy");
            } catch (Exception _) {
            }

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
        } catch (MalformedURLException e) {
            throw new FinanceNotFoundException("Gpw data not found");
        } catch (Exception e) {
            throw new FinanceNotFoundException("Gpw data: " + e.getMessage());
        } finally {
            if (driver != null) {
                driver.quit();
            }
            System.gc();
        }
        log.info("Finished updating Gpw data");
    }
}