import {KeycloakUserCreateRequest} from "./keycloak-user-create-request";
import {LanguageType} from "../../../../shared/enum/language-type";
import {CurrencyType} from "../../../../shared/enum/currency-type";

export interface KeycloakUserRegisterRequest {
    language: LanguageType;
    currency: CurrencyType;
    userCreateRequest: KeycloakUserCreateRequest
}
