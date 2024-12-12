import {Injectable} from '@angular/core';
import {HttpEvent, HttpHandler, HttpHeaders, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {from, Observable, switchMap} from 'rxjs';
import {KeycloakService} from 'keycloak-angular';

@Injectable()
export class TokenInterceptor implements HttpInterceptor {
  constructor(private keycloakService: KeycloakService) {}

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return from(this.keycloakService.getToken()).pipe(
      switchMap((token) => {
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
