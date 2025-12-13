import packageInfo from '../../package.json';

export const environment = {
    appVersion: packageInfo.version,
    production: true,
    baseUrl: 'https://muybien.pl/api/v1',
    keycloakUrl: 'https://auth.muybien.pl',
    keycloakRealm: 'marketsync-client',
    keycloakClientId: 'frontend-client',
    wsWalletUrl: 'wss://muybien.pl/api/ws-wallet'
};
