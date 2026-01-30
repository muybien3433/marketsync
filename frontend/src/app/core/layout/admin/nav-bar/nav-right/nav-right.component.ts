import {Component, HostListener, inject, OnDestroy, OnInit} from '@angular/core';
import {NgbDropdown, NgbDropdownConfig, NgbDropdownMenu, NgbDropdownToggle} from '@ng-bootstrap/ng-bootstrap';
import {NgClass} from "@angular/common";
import screenfull from "screenfull";
import {TranslatePipe, TranslateService} from "@ngx-translate/core";
import {AuthState} from "../../../../state/auth-state";

@Component({
    selector: 'app-nav-right',
    standalone: true,
    imports: [
        NgbDropdownToggle,
        NgbDropdownMenu,
        NgbDropdown,
        NgClass,
        TranslatePipe,
    ],
    templateUrl: './nav-right.component.html',
    styleUrls: ['./nav-right.component.scss'],
    providers: [NgbDropdownConfig]
})
export class NavRightComponent implements OnInit, OnDestroy {
    private readonly translate = inject(TranslateService);
    private readonly authStage = inject(AuthState)

    screenFull = true;
    isMenuOpen = false;

    constructor() {
        const config = inject(NgbDropdownConfig);
        config.placement = 'bottom-right';

        const savedLang = localStorage.getItem('selectedLanguage') || 'pl';
        this.translate.addLangs(['pl', 'en']);
        this.translate.setDefaultLang('pl');
        this.translate.use(savedLang);
    }

    ngOnInit() {
        if (screenfull.isEnabled) {
            this.screenFull = screenfull.isFullscreen;
            screenfull.on('change', () => {
                this.screenFull = screenfull.isFullscreen;
            });
        }
    }

    ngOnDestroy() {
        if (screenfull.isEnabled) {
            screenfull.off('change', () => {
                this.screenFull = screenfull.isFullscreen;
            });
        }
    }

    useLanguage(lang: string) {
        localStorage.setItem('selectedLanguage', lang);
        this.translate.use(lang).subscribe(() => {
            window.location.reload();
        });
        this.isMenuOpen = false;
    }

    @HostListener('document:click', ['$event'])
    onClick(event: MouseEvent) {
        const target = event.target as HTMLElement;
        if (!target.closest('.language-picker')) {
            this.isMenuOpen = false;
        }
    }

    toggleFullscreen() {
        if (screenfull.isEnabled) {
            screenfull.toggle().then(() => {
                this.screenFull = screenfull.isFullscreen;
            });
        }
    }

    logout() {
        this.authStage.logout();
    }
}
