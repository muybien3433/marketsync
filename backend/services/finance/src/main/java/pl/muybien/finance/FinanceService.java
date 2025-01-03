package pl.muybien.finance;

import reactor.core.publisher.Mono;

public interface FinanceService {
    Mono<Finance> fetchCurrentFinance();
}
