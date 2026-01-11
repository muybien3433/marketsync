import {inject, Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {KeycloakUserLoginRequest} from "../model/iam/request/keycloak-user-login-request";
import {Observable} from "rxjs";
import {KeycloakUserChangeEmailRequest} from "../model/iam/request/keycloak-user-change-email-request";
import {KeycloakUserChangePasswordRequest} from "../model/iam/request/keycloak-user-change-password-request";
import {KeycloakUserRegisterRequest} from "../model/iam/request/keycloak-user-register-request";
import {KeycloakUserLogin} from "../model/iam/model/keycloak-user-login.model";
import {KeycloakEmailChanged} from "../model/iam/model/keycloak-email-changed.model";
import {KeycloakUserCreated} from "../model/iam/model/keycloak-user-created.model";
import {environment} from "../../../environments/environment";

@Injectable({ providedIn: 'root' })
export class UserService {
  private readonly http = inject(HttpClient);

  private readonly usersBaseUrl = '/users';

  login(request: KeycloakUserLoginRequest): Observable<KeycloakUserLogin> {
    return this.http.post<KeycloakUserLogin>(`${environment.baseUrl}${this.usersBaseUrl}/login`, request);
  }

  changeEmailAsUser(request: KeycloakUserChangeEmailRequest): Observable<KeycloakEmailChanged> {
    return this.http.post<KeycloakEmailChanged>(`${this.usersBaseUrl}/change-email`, request);
  }

  changePasswordAsUser(request: KeycloakUserChangePasswordRequest): Observable<void> {
    return this.http.post<void>(`${this.usersBaseUrl}/change-password`, request);
  }

  register(request: KeycloakUserRegisterRequest): Observable<KeycloakUserCreated> {
    return this.http.post<KeycloakUserCreated>(`${environment.baseUrl}${this.usersBaseUrl}/register`, request);
  }
}