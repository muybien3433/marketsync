import {KeycloakUserCreateRequest} from "./keycloak-user-create-request";
import {LanguageType} from "../../../enum/language-type";
import {CurrencyType} from "../../../enum/currency-type";

export interface KeycloakUserRegisterRequest {
    language: LanguageType;
    currency: CurrencyType;
    userCreateRequest: KeycloakUserCreateRequest
}
