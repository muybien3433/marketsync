package pl.muybien.finance.finance;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/finances")
@RequiredArgsConstructor
public class FinanceController {

    private final FinanceServiceFactory financeServiceFactory;

    @GetMapping("/{uri}")
    public ResponseEntity<Mono<Finance>> getFinance(
            @PathVariable String uri
    ) {
        return ResponseEntity.ok(financeServiceFactory.getService(uri).fetchCurrentFinance());
    }
}
