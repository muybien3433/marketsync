import {Routes} from "@angular/router";

export const SUBSCRIPTION_ROUTES: Routes = [
    {
        path: '',
        children: [
            {
                path: '',
                loadComponent:() => import('./pages/subscription-list/subscription-list.component').then(m => m.default),
            },
            {
                path: 'add',
                loadComponent:() => import('./pages/subscription-add/subscription-add.component').then(m => m.default),
            },
        ],
    },
];