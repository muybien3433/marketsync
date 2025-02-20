package pl.muybien.customer;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
        name = "customer-service",
        url = "${application.config.customer-url}"
)
public interface CustomerClient {

    @GetMapping
    CustomerResponse fetchCustomerFromHeader(
            @RequestHeader(value = "Authorization") String authorizationHeader
    );
}

