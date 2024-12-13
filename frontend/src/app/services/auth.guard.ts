import {CanActivateFn} from '@angular/router';
import {inject} from '@angular/core';
import {KeycloakService} from 'keycloak-angular';

export const AuthGuard: CanActivateFn = async () => {
  const keycloakService = inject(KeycloakService);
  const userIsNotLoggedIn = !keycloakService.isLoggedIn();

  if (userIsNotLoggedIn) {
    await keycloakService.login();
    return false;
  }
  return true;
}
