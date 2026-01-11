import {Component, OnInit, inject} from '@angular/core';
import {NavigationEnd, Router, RouterModule} from '@angular/router';
import {TranslateService} from "@ngx-translate/core";

@Component({
    selector: 'app-root',
    imports: [RouterModule],
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
    private router = inject(Router);
    title = 'datta-able';

    constructor(private translate: TranslateService) {
        this.translate.setDefaultLang('pl');
        this.translate.use('pl');
    }

    ngOnInit() {
        this.router.events.subscribe((evt) => {
            if (!(evt instanceof NavigationEnd)) {
                return;
            }
            window.scrollTo(0, 0);
        });
    }
}
