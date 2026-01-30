import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import {TokenService} from "../../services/token.service";

export const tokenInterceptor: HttpInterceptorFn = (req, next) => {
    if (!/\/api\/v1\//i.test(req.url)) return next(req);

    const token = inject(TokenService).getAccessToken();

    if (!token) return next(req);

    return next(
        req.clone({
            setHeaders: { Authorization: `Bearer ${token}` }
        })
    );
};
