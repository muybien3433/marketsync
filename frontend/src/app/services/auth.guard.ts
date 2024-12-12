import {CanActivateFn, Router} from '@angular/router';
import {inject} from '@angular/core';
import {KeycloakService} from 'keycloak-angular';

/**
 * This guard return true if the user is logged in
 * This guard can be further modifier to:
 * * * check user roles using keycloakService.isUserInRole() function
 */
export const AuthGuard: CanActivateFn = (route, state) => {
  const keycloakService = inject(KeycloakService);
  const router = inject(Router);

  return keycloakService.isLoggedIn();
}
