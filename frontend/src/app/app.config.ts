import {
  APP_INITIALIZER,
  ApplicationConfig,
  importProvidersFrom,
  provideZoneChangeDetection, Provider
} from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import { provideClientHydration } from '@angular/platform-browser';
import {TranslateLoader, TranslateModule} from '@ngx-translate/core';
import {TranslateHttpLoader} from '@ngx-translate/http-loader';
import {
  HTTP_INTERCEPTORS,
  HttpClient,
  provideHttpClient, withFetch,
  withInterceptorsFromDi
} from '@angular/common/http';
import {KeycloakService} from 'keycloak-angular';
import {TokenInterceptor} from './services/token-interceptor';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import {environment} from '../environments/environment.development';

const httpLoaderFactory: (http: HttpClient) => TranslateHttpLoader = (http: HttpClient) =>
  new TranslateHttpLoader(http, './i18n/', '.json');

const Keycloak = typeof window !== 'undefined' ? import('keycloak-js') : null;

export function initializeKeycloak(keycloak: KeycloakService): () => Promise<boolean> {
  if (Keycloak !== null) {
    return () =>
      keycloak.init({
        config: {
          url: environment.keycloakUrl,
          realm: environment.keycloakRealm,
          clientId: environment.keycloakClientId
        },
        initOptions: {
          onLoad: 'check-sso',
          checkLoginIframe: false,
        },
        bearerExcludedUrls: ['']
      }).then(() => {
        console.log('Keycloak Initialized');
        return true;
      }).catch((error) => {
        console.error(error);
        return false;
      });
  } else {
    return () =>
      new Promise<boolean>((resolve) => resolve(true));
  }
}

const KeycloakInitializerProvider: Provider = {
  provide: APP_INITIALIZER,
  useFactory: initializeKeycloak,
  multi: true,
  deps: [KeycloakService]
}

const KeycloakBearerInterceptorProvider: Provider = {
  provide: HTTP_INTERCEPTORS,
  useClass: TokenInterceptor,
  multi: true
};

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideClientHydration(),
    provideHttpClient(withInterceptorsFromDi(), withFetch()),
    KeycloakService,
    KeycloakInitializerProvider,
    KeycloakBearerInterceptorProvider,
    importProvidersFrom([TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useFactory: httpLoaderFactory,
        deps: [HttpClient],
      },
    }),
    ]), provideAnimationsAsync()
  ],
};
