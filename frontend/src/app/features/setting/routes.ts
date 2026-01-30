import {Routes} from "@angular/router";

export const SETTING_ROUTES: Routes = [
    {
        path: '',
        children: [
            {
                path: 'list',
                loadComponent: () =>
                    import('./pages/setting-list/setting-list.component').then(m => m.default),
            },
        ],
    },
];
