import {inject, Injectable} from "@angular/core";
import {KeycloakUserLoginResponse} from "../../features/auth/models/response/keycloak-user-login-response";
import { KeycloakUserLoginRequest } from "../../features/auth/models/request/keycloak-user-login-request";
import {StorageScope, StorageService} from "./storage.service";

@Injectable({
    providedIn: 'root'
})
export class TokenService {
    private readonly storageService = inject(StorageService);

    private readonly accessTokenKey = 'access_token';
    private readonly refreshTokenKey = 'refresh_token';
    private readonly tokenScopeKey = 'token_scope'

    store(request: KeycloakUserLoginRequest, response: KeycloakUserLoginResponse) {
        const scope: StorageScope = request.rememberMe ? 'local' : 'session';

        this.storageService.set(this.accessTokenKey, response.access_token, scope);
        this.storageService.set(this.refreshTokenKey, response.refresh_token, scope);
        this.storageService.set(this.tokenScopeKey, scope, 'local');

        this.storageService.remove(this.accessTokenKey, scope === 'local' ? 'session' : 'local');
        this.storageService.remove(this.refreshTokenKey, scope === 'local' ? 'session' : 'local');
    }

    getAccessToken(): string | null {
        return this.storageService.getString(this.accessTokenKey);
    }

    getRefreshToken(): string | null {
        return this.storageService.getString(this.refreshTokenKey);
    }

    getTokenScope(): StorageScope | null {
        return this.storageService.get<StorageScope>(this.tokenScopeKey, 'local');
    }

    clear(): void {
        this.storageService.remove(this.accessTokenKey);
        this.storageService.remove(this.refreshTokenKey);
        this.storageService.remove(this.tokenScopeKey, 'local');
    }
}