import { CanActivateFn } from '@angular/router';
import { inject } from '@angular/core';
import { KeycloakService } from 'keycloak-angular';

export const AuthGuard: CanActivateFn = async () => {
  try {
    const keycloakService = inject(KeycloakService);

    if (!keycloakService.getKeycloakInstance()) {
      return false;
    }

    const isLoggedIn = keycloakService.isLoggedIn();
    if (!isLoggedIn) {
      await keycloakService.login();
      return false;
    }

    return true;
  } catch (error) {
    console.error('Error in AuthGuard during authentication check:', error);
    return false;
  }
};
