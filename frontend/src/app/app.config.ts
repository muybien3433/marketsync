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
  provideHttpClient,
  withInterceptorsFromDi
} from '@angular/common/http';
import {KeycloakService} from 'keycloak-angular';
import {TokenInterceptor} from './services/token-interceptor';

const httpLoaderFactory: (http: HttpClient) => TranslateHttpLoader = (http: HttpClient) =>
  new TranslateHttpLoader(http, './i18n/', '.json');

const Keycloak = typeof window !== 'undefined' ? import('keycloak-js') : null;

export function initializeKeycloak(keycloak: KeycloakService): () => Promise<boolean> {
  if (Keycloak !== null) {
    return () =>
      keycloak.init({
        config: {
          url: 'http://localhost:8880',
          realm: 'master',
          clientId: 'angular-client'
        },
        initOptions: {
          onLoad: 'login-required',
          checkLoginIframe: false,
        },
      }).then(() => {
        console.log('Keycloak Initialized');
        return true;
      }).catch((error) => {
        console.error('Keycloak Initialization Failed', error);
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
    provideHttpClient(withInterceptorsFromDi()),
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
    ])
  ],
};
