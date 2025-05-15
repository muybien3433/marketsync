package pl.muybien.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.Collections;

@Component
@RequiredArgsConstructor
@Slf4j
public class SeleniumHandler {
    private static final String REMOTE_WEB_DRIVER = "http://selenium-chrome:4444/wd/hub";

    public static WebDriver getDriverAndNavigate(String url) {
        try {
            WebDriver driver = new RemoteWebDriver(new URL(REMOTE_WEB_DRIVER), createChromeOptions());
            driver.get(url);
            return driver;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static WebDriverWait getDriverWait(WebDriver driver, Duration duration) {
        return new WebDriverWait(driver, duration);
    }

    public static void handleCookieConsent(WebDriverWait wait, String cssSelector) {
        try {
            WebElement acceptButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(cssSelector)));
            acceptButton.click();
            log.debug("Cookie consent accepted.");
        } catch (TimeoutException e) {
            log.debug("No cookie consent found.");
        }
    }

    public static void handleCookieConsent(WebDriverWait wait, By by) {
        try {
            WebElement acceptButton = wait.until(ExpectedConditions.elementToBeClickable(by));
            acceptButton.click();
            log.debug("Cookie consent accepted.");
        } catch (TimeoutException e) {
            log.debug("No cookie consent found.");
        }
    }

    private static ChromeOptions createChromeOptions() {
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

        return options;
    }
}
