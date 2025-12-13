import packageInfo from "../../package.json";

export const environment = {
    appVersion: packageInfo.version,
    production: false,
    baseUrl: 'http://localhost:9999/api/v1',
    keycloakUrl: 'http://localhost:8080',
    keycloakRealm: 'marketsync-client',
    keycloakClientId: 'frontend-client',
    wsWalletUrl: 'http://localhost:9999/ws-wallet'
}