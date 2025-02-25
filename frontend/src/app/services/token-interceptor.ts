import { Injectable } from '@angular/core';
import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { catchError, from, Observable, of, switchMap } from 'rxjs';
import { KeycloakService } from 'keycloak-angular';

@Injectable()
export class TokenInterceptor implements HttpInterceptor {
    constructor(private keycloakService: KeycloakService) {}

    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        return of(this.keycloakService.isLoggedIn()).pipe(
            switchMap((isLoggedIn: boolean) => {
                if (isLoggedIn) {
                    return of(this.keycloakService.isTokenExpired()).pipe(
                        switchMap((isExpired: boolean) => {
                            if (isExpired) {
                                return from(this.keycloakService.updateToken(70)).pipe(
                                    switchMap(() => this.getTokenAndSendRequest(request, next))
                                );
                            } else {
                                return this.getTokenAndSendRequest(request, next);
                            }
                        }),
                        catchError((error) => {
                            console.error('Error checking token expiration:', error);
                            return next.handle(request);
                        })
                    );
                } else {
                    return next.handle(request);
                }
            }),
            catchError((error) => {
                console.error('Error checking login status:', error);
                return next.handle(request);
            })
        );
    }

    private getTokenAndSendRequest(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        return from(this.keycloakService.getToken()).pipe(
            switchMap((token: string | undefined) => {
                if (token) {
                    const authReq = request.clone({
                        setHeaders: {
                            Authorization: `Bearer ${token}`,
                        },
                    });
                    return next.handle(authReq);
                }
                return next.handle(request);
            })
        );
    }
}
