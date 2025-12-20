package pl.muybien.feign;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(
        name = "iam-service",
        url = "${application.config.iam-url}"
)
public interface IamClient {

}
