package pl.muybien.feign;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import pl.muybien.dto.iam.request.KeycloakUserCreateRequest;
import pl.muybien.dto.iam.request.KeycloakUserLoginRequest;
import pl.muybien.dto.iam.response.KeycloakUserCreatedResponse;
import pl.muybien.dto.iam.response.KeycloakUserLoginResponse;

@FeignClient(
        name = "iam-service",
        url = "${application.config.iam-url}"
)
@ConditionalOnProperty(name = "application.config.iam-url")
public interface IamClient {

    @PostMapping("/users")
    KeycloakUserCreatedResponse register(@RequestBody KeycloakUserCreateRequest request);

    @PostMapping("/auth/login")
    KeycloakUserLoginResponse login(@RequestBody KeycloakUserLoginRequest request);
}
