import { inject, Injectable } from '@angular/core';
import Keycloak from 'keycloak-js';

@Injectable({ providedIn: 'root' })
export class WsTokenService {
    private readonly keycloak = inject(Keycloak);

    async getFreshToken(minValiditySeconds: number): Promise<string> {
        if (!this.keycloak.authenticated) {
            throw new Error('Not logged in');
        }

        await this.keycloak.updateToken(minValiditySeconds);

        const token = this.keycloak.token;
        if (!token) {
            throw new Error('Token missing after refresh');
        }

        return token;
    }

    async buildAuthenticatedUrl(baseUrl: string, minValiditySeconds = 70): Promise<string> {
        const token = await this.getFreshToken(minValiditySeconds);
        const separator = baseUrl.includes('?') ? '&' : '?';
        return `${baseUrl}${separator}token=${encodeURIComponent(token)}`;
    }
}
