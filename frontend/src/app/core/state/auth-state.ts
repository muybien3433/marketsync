import {inject, Injectable} from "@angular/core";
import {TokenService} from "../services/token.service";
import {BehaviorSubject} from "rxjs";
import {Router} from "@angular/router";

@Injectable({ providedIn: 'root' })
export class AuthState {
    private readonly tokenService = inject(TokenService);
    private readonly router = inject(Router);

    private readonly authenticatedSubject$ = new BehaviorSubject<boolean>(this.hasToken());
    readonly authenticated$ = this.authenticatedSubject$.asObservable();

    syncFromStorage(): void {
        this.authenticatedSubject$.next(this.hasToken());
    }

    setLoggedIn(): void {
        this.authenticatedSubject$.next(true);
    }

    logout(): void {
        this.tokenService.clear();
        this.authenticatedSubject$.next(false);
        this.router.navigateByUrl('/auth/login');
    }

    isLoggedIn(): boolean {
        return this.hasToken();
    }

    private hasToken(): boolean {
        const token = this.tokenService.getAccessToken();;
        return !!token && !this.isExpired(token);
    }

    private isExpired(token: string): boolean {
        const payload = token.split('.')[1];
        if (!payload) return true;

        try {
            const json = JSON.parse(atob(payload.replace(/-/g, '+').replace(/_/g, '/')));
            const exp = json.exp;
            if (!exp) return true;
            return Date.now() >= exp * 1000;
        } catch {
            return true;
        }
    }
}