package io.platform.dto.iam.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class KeycloakErrorResponse {

    private String error;

    @JsonProperty("error_description")
    private String errorDescription;

}
