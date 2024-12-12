import {Component} from '@angular/core';
import {TranslatePipe, TranslateService} from "@ngx-translate/core";
import {Router} from '@angular/router';
import {KeycloakService} from 'keycloak-angular';
import {NgIf} from '@angular/common';

@Component({
  selector: 'app-menu',
  standalone: true,
  imports: [
    TranslatePipe,
    NgIf
  ],
  templateUrl: './menu.component.html',
  styleUrl: './menu.component.css'
})
export class MenuComponent {
  constructor(private router: Router, private translate: TranslateService, private keycloakService: KeycloakService) {
    this.translate.addLangs(['pl', 'en']);
    this.translate.setDefaultLang('pl');
    this.translate.use('pl');
  }

  isLoggedIn() {
    return this.keycloakService.isLoggedIn();
  }

  useLanguage(language: string): void {
    this.translate.use(language);
  }

  main() {
    this.router.navigate(['/']);
  }

  async login() {
    await this.keycloakService.login();
  }

  async register() {
    await this.keycloakService.register()
  }

  logout(): void {
    this.keycloakService.logout();
  }

  wallet() {
    this.router.navigate(['wallet']);
  }

  subscription() {
    this.router.navigate(['subscription']);
  }
}
