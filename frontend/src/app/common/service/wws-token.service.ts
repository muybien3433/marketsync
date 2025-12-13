import { Injectable } from '@angular/core';
import { KeycloakService } from 'keycloak-angular';

@Injectable({
    providedIn: 'root'
})
export class WsTokenService {

    constructor(private keycloak: KeycloakService) {}

    async buildAuthenticatedUrl(baseUrl: string): Promise<string> {
        try {
            const isLoggedIn = await this.keycloak.isLoggedIn();
            if (!isLoggedIn) {
                return baseUrl;
            }

            const token = await this.keycloak.getToken();
            if (!token) {
                return baseUrl;
            }

            const separator = baseUrl.includes('?') ? '&' : '?';
            return `${baseUrl}${separator}token=${encodeURIComponent(token)}`;
        } catch (e) {
            console.error('WS token resolve failed', e);
            return baseUrl;
        }
    }
}
