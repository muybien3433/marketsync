package pl.muybien.finance.finance.crypto.usdcoin;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import pl.muybien.finance.exception.FinanceNotFoundException;
import pl.muybien.finance.finance.Finance;
import pl.muybien.finance.finance.FinanceService;
import reactor.core.publisher.Mono;

@Service("usdcoin")
@RequiredArgsConstructor
public class UsdcoinService implements FinanceService {

    private final WebClient.Builder webClientBuilder;

    @Value("${api.usdcoin.url}")
    private String url;
    @Value("${spring.application.name}")
    private String serviceName;

    @Override
    public Mono<Finance> fetchCurrentFinance() {
        return webClientBuilder.baseUrl(url).build().get()
                .retrieve()
                .bodyToMono(UsdcoinResponse.class)
                .switchIfEmpty(Mono.error(new FinanceNotFoundException(
                        "Could not fetch currency for: %s".formatted(serviceName))))
                .map(response -> {
                            var data = response.data();
                            return Finance.builder()
                                    .name(data.name())
                                    .priceUsd(data.priceUsd())
                                    .build();
                        }
                );
    }
}