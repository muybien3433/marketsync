package pl.muybien.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import pl.muybien.enumeration.AssetType;
import pl.muybien.enumeration.CurrencyType;
import pl.muybien.response.FinanceResponse;

import java.math.BigDecimal;

@FeignClient(
        name = "finance-service",
        url = "${application.config.finance-url}"
)
public interface FinanceClient {

    @GetMapping("/{asset-type}/{uri}")
    FinanceResponse findFinanceByTypeAndUri(
            @PathVariable("asset-type") AssetType assetType,
            @PathVariable("uri") String uri
    );

    @GetMapping("/currencies/{from}/{to}")
    BigDecimal findExchangeRate(
            @PathVariable("from") CurrencyType from,
            @PathVariable("to") CurrencyType to
    );
}
