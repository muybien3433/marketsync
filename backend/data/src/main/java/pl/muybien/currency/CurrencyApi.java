package pl.muybien.currency;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.muybien.entity.Currency;
import pl.muybien.enumeration.CurrencyType;
import pl.muybien.enumeration.UnitType;
import pl.muybien.repository.CurrencyRepository;
import pl.muybien.updater.QueueUpdater;

import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CurrencyApi extends QueueUpdater {
    private static final String TARGET_URL = "https://api.currencyapi.com/v3/latest";

    private final CurrencyRepository currencyRepository;
    private final ObjectMapper objectMapper;

    @Override
    @EventListener(ApplicationReadyEvent.class)
    @Scheduled(fixedDelay = 14400000) // 4 hours for free api key TODO: fetch from config
    public void scheduleUpdate() {
        enqueueUpdate("currency-api");
    }

    @Override
    public void updateAssets() {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(TARGET_URL).openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(10_000);
            conn.setReadTimeout(15_000);
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("User-Agent", "muybien-data-service/1.0");
            conn.setRequestProperty("apikey", "cur_live_iRfBC5llXvpTZaqiM4YGvXBiMP5HBLYSr4qBtOH0"); // TODO: fetch from config

            int status = conn.getResponseCode();
            try (InputStream is = status >= 200 && status < 300 ? conn.getInputStream() : conn.getErrorStream()) {
                Map<String, Object> root = objectMapper.readValue(is, new TypeReference<>() {
                });
                if (status >= 200 && status < 300) {
                    Map<String, Object> meta = (Map<String, Object>) root.get("meta");
                    Map<String, Map<String, Object>> data = (Map<String, Map<String, Object>>) root.get("data");
                    String updatedAt = meta != null ? String.valueOf(meta.get("last_updated_at")) : null;

                    LocalDateTime lastUpdated;
                    try {
                        lastUpdated = updatedAt != null ? OffsetDateTime.parse(updatedAt).toLocalDateTime() : LocalDateTime.now(ZoneOffset.UTC);
                    } catch (Exception e) {
                        lastUpdated = LocalDateTime.now(ZoneOffset.UTC);
                    }

                    log.debug("currencyapi last_updated_at={}", updatedAt);
                    log.debug("currencyapi currencies_count={}", data != null ? data.size() : 0);

                    List<Currency> currencies = new ArrayList<>();
                    if (data != null) {
                        LocalDateTime finalLastUpdated = lastUpdated;
                        data.forEach((k, v) -> {
                            if (CurrencyType.fromString(k).isPresent()) {
                                Object value = v.get("value");
                                if (value instanceof Number) {
                                    BigDecimal rate = new BigDecimal(value.toString());
                                    Currency currency = Currency.builder()
                                            .name(CurrencyType.valueOf(k))
                                            .exchangeFromUSD(rate)
                                            .unitType(UnitType.UNIT)
                                            .lastModifiedDate(finalLastUpdated)
                                            .build();
                                    currencies.add(currency);
                                }
                            }
                        });
                    }

                    if (!currencies.isEmpty()) {
                        currencies.forEach(currency -> log.debug(currency.toString()));
                        currencyRepository.saveAll(currencies);
                    }
                } else {
                    String msg = objectMapper.writeValueAsString(root);
                    log.error("currencyapi error status={} body={}", status, msg);
                }
            }
        } catch (Exception e) {
            log.error("CurrencyApi update failed", e);
        } finally {
            if (conn != null) conn.disconnect();
        }
    }
}