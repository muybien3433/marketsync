import packageInfo from '../../package.json';

export const environment = {
    appVersion: packageInfo.version,
    production: true,
    baseUrl: 'https://muybien.pl/api/v1',
    keycloakUrl: 'https://auth.muybien.pl:8080',
    keycloakRealm: 'marketsync-client',
    keycloakClientId: 'frontend-client',
    wsWalletUrl: 'https://muybien.pl/ws-wallet'
};
