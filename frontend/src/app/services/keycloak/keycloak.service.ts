import {Inject, Injectable, PLATFORM_ID} from '@angular/core';
import Keycloak from 'keycloak-js';
import {isPlatformBrowser} from '@angular/common';
import {UserProfile} from './user-profile';

@Injectable({
  providedIn: 'root'
})
export class KeycloakService {

  private _keycloak: Keycloak | undefined;
  private _profile: UserProfile | undefined;

  get keycloak() {
    if (!this._keycloak && isPlatformBrowser(this.platformId)) {
      this._keycloak = new Keycloak({
        url: 'http://localhost:8880',
        realm: 'marketsync',
        clientId: 'marketsync'
      })
    }
    return this._keycloak;
  }

  getProfile() {
    return this._profile;
  }

  constructor(@Inject(PLATFORM_ID) private platformId: Object) { }

  async init() {
    if (isPlatformBrowser(this.platformId)) {
      const authenticated = await this.keycloak?.init({
        onLoad: 'login-required',
      });

      if (authenticated) {
        this._profile = (await this.keycloak?.loadUserProfile()) as UserProfile;
        this._profile.token = this.keycloak?.token;
      }
    }
  }

  login() {
    return this.keycloak?.login();
  }

  logout() {
    return this.keycloak?.logout({redirectUri: 'http://localhost:4200'});
  }
}
