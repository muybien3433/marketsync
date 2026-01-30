import {Routes} from "@angular/router";

export const AUTH_ROUTES: Routes = [
    {
        path: '',
        children: [
            {
                path: 'login',
                loadComponent: () =>
                    import('./pages/login/login.component').then(m => m.LoginComponent),
            },
            {
                path: 'password-change',
                loadComponent: () =>
                    import('./pages/password-change/password-change.component').then(m => m.PasswordChangeComponent),
            },
            {
                path: 'password-forgot',
                loadComponent: () =>
                    import('./pages/password-forgot/password-forgot.component').then(m => m.PasswordForgotComponent),
            },
            {
                path: 'register',
                loadComponent: () =>
                    import('./pages/register/register.component').then(m => m.RegisterComponent),
            },
            {
                path: 'register-success',
                loadComponent: () =>
                    import('./pages/register-success/register-success.component').then(m => m.RegisterSuccessComponent),
            },
        ],
    },
];
