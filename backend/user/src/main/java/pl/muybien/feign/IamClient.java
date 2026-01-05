package pl.muybien.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import pl.muybien.dto.iam.request.KeycloakUserCreateRequest;
import pl.muybien.dto.iam.response.KeycloakUserCreatedResponse;

@FeignClient(
        name = "iam-service",
        url = "${application.config.iam-url}"
)
public interface IamClient {

    @PostMapping
    KeycloakUserCreatedResponse createUser(@RequestBody KeycloakUserCreateRequest request);
}
