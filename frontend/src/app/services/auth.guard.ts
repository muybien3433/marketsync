import {CanActivateFn} from '@angular/router';
import {inject} from '@angular/core';
import {KeycloakService} from 'keycloak-angular';

export const AuthGuard: CanActivateFn = async () => {
  const keycloakService = inject(KeycloakService);

  try {
    const isLoggedIn = keycloakService.isLoggedIn();

    if (!isLoggedIn) {
      console.warn('User is not logged in, redirecting to Keycloak login...');
      await keycloakService.login();
      return false;
    }

    return true;
  } catch (error) {
    console.error('Error checking Keycloak authentication:', error);
    await keycloakService.login();
    return false;
  }
};
