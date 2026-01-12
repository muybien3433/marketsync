import {CanActivateFn, Router} from "@angular/router";
import Keycloak from "keycloak-js";
import {inject} from "@angular/core";

export const authGuard: CanActivateFn = async () => {
    const keycloak = inject(Keycloak);
    const router = inject(Router);

    if (keycloak.authenticated) {
        return true;
    }

    return router.parseUrl('/auth/login');
};
