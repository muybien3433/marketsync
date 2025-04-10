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
public class NewconnectScraper extends FinanceUpdater {

    private final static String TARGET_URL = "https://newconnect.pl/notowania";
    private final static String REMOTE_WEB_DRIVER = "http://selenium-chrome:4444/wd/hub";

    private final FinanceDatabaseUpdater databaseUpdater;

    @Override
    @EventListener(ApplicationReadyEvent.class)
    @Scheduled(fixedDelay = 300000)
    public void scheduleUpdate() {
        enqueueUpdate("new-connect");
    }

    @Override
    @Transactional
    public void updateAssets() {
        log.info("Starting the update of NewConnect data...");
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

            try {
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

                WebElement acceptCookiesButton = wait.until(ExpectedConditions.elementToBeClickable
                        (By.xpath("//button[contains(text(),'Akceptuję')]")));
                acceptCookiesButton.click();
            } catch (Exception _) {}

            WebElement body = driver.findElement(By.tagName("body"));
            String pageContent = body.getText();

            int indexOfFirst = pageContent.indexOf("AD");
            int indexOfLast = pageContent.lastIndexOf("Razem");
            if (indexOfFirst != -1 && indexOfLast != -1) {
                pageContent = pageContent.substring(indexOfFirst, indexOfLast);
                pageContent = pageContent.replaceAll("/\\S+", "");
                pageContent = pageContent.replaceAll("\\bNC\\s+\\w+\\b", "");
                pageContent = pageContent.trim().replaceAll(" {2}", " ");
            }

            String regex =
                    "([A-Za-z]+)\\s"
                            + "([A-Za-z]+)\\s"
                            + "([0-9]{2}:[0-9]{2}:[0-9]{2}|-)\\s"
                            + "([0-9,]+|-)\\s"
                            + "([0-9,]+|-)\\s"
                            + "([0-9,]+|-)\\s"
                            + "([0-9,]+|-)\\s"
                            + "([0-9,]+|-)\\s"
                            + "([0-9,]+|-)";

            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(pageContent);

            while (matcher.find()) {
                String name = matcher.group(1).trim();
                String uri = name.toLowerCase();
                String symbol = matcher.group(2).trim();
                String renewalRate = matcher.group(4).trim().replace(",", ".");
                String lastTransactionRate = matcher.group(9).trim().replace(",", ".");
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
            throw new FinanceNotFoundException("NewConnect data not found");
        } catch (Exception e) {
            throw new FinanceNotFoundException("NewConnect data: " + e.getMessage());
        } finally {
            if (driver != null) {
                driver.quit();
            }
            System.gc();
        }
        log.info("Finished updating NewConnect data");
    }
}