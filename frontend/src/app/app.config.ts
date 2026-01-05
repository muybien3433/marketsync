import { ApplicationConfig, importProvidersFrom, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { routes } from './app.routes';
import { BrowserModule } from '@angular/platform-browser';
import { TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { HttpClient, provideHttpClient, withFetch, withInterceptors } from '@angular/common/http';
import { provideAnimations } from '@angular/platform-browser/animations';
import {
    provideKeycloak,
    includeBearerTokenInterceptor,
    INCLUDE_BEARER_TOKEN_INTERCEPTOR_CONFIG
} from 'keycloak-angular';
import { environment } from '../environments/environment';
import Aura from '@primeng/themes/aura';
import { providePrimeNG } from 'primeng/config';

const httpLoaderFactory = (http: HttpClient): TranslateHttpLoader =>
    new TranslateHttpLoader(http, './assets/i18n/', '.json');

export const appConfig: ApplicationConfig = {
    providers: [
        provideZoneChangeDetection({ eventCoalescing: true }),
        provideRouter(routes),

        provideKeycloak({
            config: {
                url: environment.keycloakUrl,
                realm: environment.keycloakRealm,
                clientId: environment.keycloakClientId
            },
            initOptions: {
                onLoad: 'login-required',
                checkLoginIframe: false,
                flow: 'standard'
            }
        }),

        {
            provide: INCLUDE_BEARER_TOKEN_INTERCEPTOR_CONFIG,
            useValue: [{ urlPattern: /\/api\/v1/i, bearerPrefix: 'Bearer' }]
        },

        provideHttpClient(
            withFetch(),
            withInterceptors([includeBearerTokenInterceptor])
        ),

        providePrimeNG({ theme: { preset: Aura } }),

        importProvidersFrom(
            BrowserModule,
            TranslateModule.forRoot({
                loader: { provide: TranslateLoader, useFactory: httpLoaderFactory, deps: [HttpClient] },
                isolate: false
            })
        ),

        provideAnimations()
    ]
};

