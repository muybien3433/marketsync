import {
  APP_INITIALIZER,
  ApplicationConfig,
  importProvidersFrom,
  provideZoneChangeDetection, Provider
} from '@angular/core';
import { provideClientHydration } from '@angular/platform-browser';
import {TranslateLoader, TranslateModule} from '@ngx-translate/core';
import {TranslateHttpLoader} from '@ngx-translate/http-loader';
import {
  HTTP_INTERCEPTORS,
  HttpClient,
  provideHttpClient,
} from '@angular/common/http';
import {KeycloakService} from 'keycloak-angular';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import {environment} from "../environments/environment";
import {TokenInterceptor} from "./common/service/token-interceptor";
import {provideAnimations} from "@angular/platform-browser/animations";

const httpLoaderFactory: (http: HttpClient) => TranslateHttpLoader = (http: HttpClient) =>
  new TranslateHttpLoader(http, './i18n/', '.json');

function initializeKeycloak(keycloak: KeycloakService): () => Promise<boolean> {
  return async () => {
    if (typeof window === 'undefined') return true;

    try {
      await keycloak.init({
        config: {
          url: environment.keycloakUrl,
          realm: environment.keycloakRealm,
          clientId: environment.keycloakClientId
        },
        initOptions: {
          onLoad: 'check-sso',
          checkLoginIframe: false,
          flow: 'standard',
        },
        bearerExcludedUrls: ['']
      });
      return true;
    } catch (error) {
      console.error('Keycloak Initialization Failed:', error);
      return false;
    }
  };
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
    provideClientHydration(),
    provideAnimations(),
    provideHttpClient(),
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