package pl.muybien.finance;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "finance-service",
        url = "${application.config.finance-url}"
)
public interface FinanceClient {

    @GetMapping("/{type}/{uri}/{currency}")
    FinanceResponse findFinanceByUriAndTypeAndCurrency(
            @PathVariable("type") String type,
            @PathVariable("uri") String uri,
            @PathVariable("currency") String currency
    );
}
