import {CanActivateFn} from '@angular/router';
import {inject} from '@angular/core';
import {KeycloakService} from 'keycloak-angular';

export const AuthGuard: CanActivateFn = async () => {
  const keycloakService = inject(KeycloakService);

  try {
    const initialized = keycloakService.getKeycloakInstance()?.authenticated !== undefined;

    if (!initialized) {
      return false;
    }

    const isLoggedIn = keycloakService.isLoggedIn();
    if (!isLoggedIn) {
      console.info('User is not logged in. Redirecting to Keycloak login...');
      await keycloakService.login();
      return false;
    }
    return true;

  } catch (error) {
    console.error('Error in AuthGuard during authentication check:', error);
    await keycloakService.login();
    return false;
  }
};