package pl.muybien.finance.crypto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.muybien.exception.FinanceNotFoundException;
import pl.muybien.finance.AssetType;
import pl.muybien.finance.CurrencyType;
import pl.muybien.finance.FinanceDetail;
import pl.muybien.finance.UnitType;
import pl.muybien.finance.updater.FinanceDatabaseUpdater;
import pl.muybien.finance.updater.FinanceUpdater;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CoinGeckoClient extends FinanceUpdater {

    @Value("${coingecko.url}")
    private String API_URL;
    @Value("${coingecko.api-key}")
    private String API_KEY;

    private final FinanceDatabaseUpdater financeDatabaseUpdater;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Override
    @Scheduled(fixedRateString = "${coingecko.crypto-updater-frequency-ms}")
    protected void scheduleUpdate() {
        enqueueUpdate("coingecko");
    }

    @Override
    protected void updateAssets() {
        log.info("Starting the update of CoinGecko data...");
        var cryptos = fetchDataFromApi();
        if (cryptos != null && !cryptos.isEmpty()) {
            financeDatabaseUpdater.saveFinanceToDatabase(AssetType.CRYPTO.name(), cryptos);
            log.info("Finished updating CoinGecko data");
        }
    }

    public Map<String, FinanceDetail> fetchDataFromApi() {
        Map<String, FinanceDetail> cryptos = new HashMap<>();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(API_URL))
                    .header("accept", "application/json")
                    .header("x-cg-demo-api-key", API_KEY)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new FinanceNotFoundException(
                        "Error fetch CoinGecko data, status code: %s".formatted(response.statusCode()));
            }

            List<CoinGeckoDetail> details = objectMapper.readValue(response.body(), new TypeReference<>() {
            });
            details.forEach(geckoDetail -> {
                String name = geckoDetail.name();
                String symbol = geckoDetail.symbol();
                String id = geckoDetail.id();
                String currentPrice = geckoDetail.currentPrice();

                if (name == null || name.isBlank() ||
                        symbol == null || symbol.isBlank() ||
                        id == null || id.isBlank() ||
                        currentPrice == null ||
                        new BigDecimal(currentPrice).compareTo(BigDecimal.ZERO) <= 0) {
                    return;
                }

                FinanceDetail financeDetail = new FinanceDetail(
                        name,
                        symbol.toUpperCase(),
                        id,
                        UnitType.UNIT.name(),
                        currentPrice,
                        CurrencyType.USD.name(),
                        AssetType.CRYPTO.name(),
                        LocalDateTime.now()
                );
                cryptos.put(financeDetail.uri(), financeDetail);
            });
        } catch (URISyntaxException e) {
            throw new FinanceNotFoundException("Invalid API URL: " + API_URL);
        } catch (IOException e) {
            throw new FinanceNotFoundException("Failed to fetch data from CoinGecko API");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new FinanceNotFoundException("Thread interrupted while fetching data");
        }
        return cryptos;
    }

    private record CoinGeckoDetail(
            @JsonProperty("id") String id,
            @JsonProperty("symbol") String symbol,
            @JsonProperty("name") String name,
            @JsonProperty("current_price") String currentPrice,
            @JsonProperty("market_cap") long marketCap,
            @JsonProperty("market_cap_rank") int marketCapRank,
            @JsonProperty("fully_diluted_valuation") long fullyDilutedValuation,
            @JsonProperty("total_volume") long totalVolume,
            @JsonProperty("high_24h") BigDecimal high24h,
            @JsonProperty("low_24h") BigDecimal low24h,
            @JsonProperty("price_change_24h") BigDecimal priceChange24h,
            @JsonProperty("price_change_percentage_24h") BigDecimal priceChangePercentage24h,
            @JsonProperty("market_cap_change_24h") BigDecimal marketCapChange24h,
            @JsonProperty("market_cap_change_percentage_24h") BigDecimal marketCapChangePercentage24h,
            @JsonProperty("circulating_supply") BigDecimal circulatingSupply,
            @JsonProperty("total_supply") BigDecimal totalSupply,
            @JsonProperty("max_supply") BigDecimal maxSupply,
            @JsonProperty("ath") BigDecimal ath,
            @JsonProperty("ath_change_percentage") BigDecimal athChangePercentage,
            @JsonProperty("ath_date") String athDate,
            @JsonProperty("atl") BigDecimal atl,
            @JsonProperty("atl_change_percentage") BigDecimal atlChangePercentage,
            @JsonProperty("atl_date") String atlDate,
            @JsonProperty("roi") Object roi,
            @JsonProperty("last_updated") String lastUpdated
    ) {
    }
}