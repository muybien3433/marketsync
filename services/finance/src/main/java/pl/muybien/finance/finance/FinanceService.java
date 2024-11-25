package pl.muybien.finance.finance;

import reactor.core.publisher.Mono;

public interface FinanceService {
    Mono<Finance> fetchCurrentFinance();
}
