package pl.muybien.finance;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;

@FeignClient(
        name = "finance-service",
        url = "${application.config.finance-url}"
)
public interface FinanceClient {

    @GetMapping("/{asset-type}/{uri}")
    FinanceResponse findFinanceByTypeAndUri(
            @PathVariable("asset-type") String assetType,
            @PathVariable("uri") String uri
    );

    @GetMapping("/currencies/{from}/{to}")
    BigDecimal findExchangeRate(
            @PathVariable("from") String from,
            @PathVariable("to") String to
    );
}
