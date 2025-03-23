import packageInfo from "../../package.json";

export const environment = {
    appVersion: packageInfo.version,
    production: false,
    baseUrl: 'http://localhost/api/v1',
    keycloakUrl: 'http://localhost:8080',
    keycloakRealm: 'master',
    keycloakClientId: 'marketsync-client'
}