export interface KeycloakUserLogin {
  accessToken: string;
  refreshToken?: string;
  expiresIn?: number;
  refreshExpiresIn?: number;
  tokenType?: string;
  sessionState?: string;
  scope?: string;
}