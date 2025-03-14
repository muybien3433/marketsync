import {Component} from '@angular/core';
import {TranslatePipe} from "@ngx-translate/core";
import {Router} from '@angular/router';
import {KeycloakService} from 'keycloak-angular';
import {HamburgerMenuComponent} from '../hamburger-menu/hamburger-menu.component';
import {LanguageMenuComponent} from "../language-menu/language-menu.component";
import {NgIf} from "@angular/common";

@Component({
    selector: 'app-menu',
    standalone: true,
    imports: [
        TranslatePipe,
        HamburgerMenuComponent,
        LanguageMenuComponent,
        NgIf
    ],
    templateUrl: './navbar.component.html',
    styleUrl: './navbar.component.css'
})
export class NavbarComponent {
    constructor(private router: Router, private keycloakService: KeycloakService) {
    }

    isLoggedIn() {
        return this.keycloakService.isLoggedIn();
    }

    async login() {
        await this.keycloakService.login();
    }

    async register() {
        await this.keycloakService.register()
    }

    wallet() {
        this.router.navigate(['wallet']);
    }

    subscription() {
        this.router.navigate(['subscription']);
    }
}
