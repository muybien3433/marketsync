package pl.muybien.finance;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "finance-service",
        url = "${application.config.finance-url}"
)
public interface FinanceClient {

    @GetMapping("/{type}/{uri}")
    FinanceResponse findFinanceByTypeAndUri(@PathVariable String type, @PathVariable("uri") String uri);
}
