import {inject, Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {KeycloakUserLoginRequest} from "../models/request/keycloak-user-login-request";
import {Observable} from "rxjs";
import {KeycloakUserChangeEmailRequest} from "../models/request/keycloak-user-change-email-request";
import {KeycloakUserChangePasswordRequest} from "../models/request/keycloak-user-change-password-request";
import {KeycloakUserRegisterRequest} from "../models/request/keycloak-user-register-request";
import {HttpResponse} from "@angular/common/module.d-CnjH8Dlt";
import {KeycloakUserLoginResponse} from "../models/response/keycloak-user-login-response";
import {KeycloakEmailChangedResponse} from "../models/response/keycloak-email-changed-response";
import {KeycloakUserCreatedResponse} from "../models/response/keycloak-user-created-response.";
import {API_ENDPOINTS} from "../../../core/http/api/api-endpoints";
import {tap} from "rxjs/operators";
import {TokenService} from "../../../core/services/token.service";

@Injectable({providedIn: 'root'})
export class AuthApi {
    private readonly http = inject(HttpClient);
    private readonly tokenService = inject(TokenService);

    login(request: KeycloakUserLoginRequest): Observable<KeycloakUserLoginResponse> {
        return this.http
            .post<KeycloakUserLoginResponse>(`${API_ENDPOINTS.USER}/login`, request)
            .pipe(
                tap(response => {
                    this.tokenService.store(request, response);
                })
            )
    }

    register(request: KeycloakUserRegisterRequest): Observable<HttpResponse<KeycloakUserCreatedResponse>> {
        return this.http.post<KeycloakUserCreatedResponse>(
            `${API_ENDPOINTS.USER}/register`,
            request,
            {observe: 'response'}
        );
    }

    changeEmailAsUser(request: KeycloakUserChangeEmailRequest): Observable<KeycloakEmailChangedResponse> {
        return this.http.post<KeycloakEmailChangedResponse>(`${API_ENDPOINTS.USER}/change-email`, request);
    }

    changePasswordAsUser(request: KeycloakUserChangePasswordRequest): Observable<void> {
        return this.http.post<void>(`${API_ENDPOINTS.USER}/change-password`, request);
    }

}