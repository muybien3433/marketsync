import {
    APP_INITIALIZER,
    ApplicationConfig,
    importProvidersFrom,
    provideZoneChangeDetection,
    Provider
} from '@angular/core';
import { provideRouter } from '@angular/router';
import { routes } from './app.routes';
import { BrowserModule, provideClientHydration } from '@angular/platform-browser';
import {TranslateLoader, TranslateModule, TranslateService} from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import {
    HTTP_INTERCEPTORS,
    HttpClient,
    provideHttpClient,
    withFetch,
    withInterceptorsFromDi
} from '@angular/common/http';
import {KeycloakService } from 'keycloak-angular';
import { provideAnimations } from "@angular/platform-browser/animations";
import { TokenInterceptor } from "./common/service/token-interceptor";
import {environment} from "../environments/environment";

const httpLoaderFactory = (http: HttpClient): TranslateHttpLoader =>
    new TranslateHttpLoader(http, './assets/i18n/', '.json');

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
                // bearerExcludedUrls: ['/assets/i18n/']
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
};

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
        provideHttpClient(
            withInterceptorsFromDi(),
            withFetch()
        ),
        KeycloakService,
        TranslateService,
        KeycloakInitializerProvider,
        KeycloakBearerInterceptorProvider,
        importProvidersFrom(
            BrowserModule,
            TranslateModule.forRoot({
                loader: {
                    provide: TranslateLoader,
                    useFactory: httpLoaderFactory,
                    deps: [HttpClient]
                },
                isolate: false
            })
        ),
        provideAnimations()
    ],
};