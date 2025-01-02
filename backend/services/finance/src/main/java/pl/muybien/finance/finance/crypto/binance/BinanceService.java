package pl.muybien.finance.finance.crypto.binance;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import pl.muybien.finance.exception.FinanceNotFoundException;
import pl.muybien.finance.finance.Finance;
import pl.muybien.finance.finance.FinanceService;
import reactor.core.publisher.Mono;

@Service("binance")
@RequiredArgsConstructor
public class BinanceService implements FinanceService {

    private final WebClient.Builder webClientBuilder;

    @Value("${crypto.api.binance.url}")
    private String url;
    @Value("${crypto.api.binance.uri}")
    private String serviceName;

    @Override
    public Mono<Finance> fetchCurrentFinance() {
        return webClientBuilder.baseUrl(url).build().get()
                .retrieve()
                .bodyToMono(BinanceResponse.class)
                .switchIfEmpty(Mono.error(new FinanceNotFoundException(
                        "Could not fetch currency for: %s".formatted(serviceName))))
                .map(response -> {
                            var data = response.data();
                            return Finance.builder()
                                    .name(data.name())
                                    .price(data.priceUsd())
                                    .build();
                        }
                );
    }
}
