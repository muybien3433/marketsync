import { Component } from '@angular/core';
import {TranslatePipe, TranslateService} from "@ngx-translate/core";
import {Router} from '@angular/router';
import {KeycloakService} from '../../services/keycloak/keycloak.service';

@Component({
  selector: 'app-menu',
  standalone: true,
    imports: [
        TranslatePipe
    ],
  templateUrl: './menu.component.html',
  styleUrl: './menu.component.css'
})
export class MenuComponent {
  constructor(private router: Router, private translate: TranslateService,
              private keycloakService: KeycloakService) {
    this.translate.addLangs(['pl', 'en']);
    this.translate.setDefaultLang('pl');
    this.translate.use('pl');
  }

  useLanguage(language: string): void {
    this.translate.use(language);
  }

  main() {
    this.router.navigate(['/']);
  }

  logIn() {
    this.router.navigate(['login']);
  }

  signUp() {
    this.router.navigate(['register']);
  }

  async logOut() {
    this.keycloakService.keycloak?.logout();
  }

  wallet() {
    this.router.navigate(['wallet']);
  }

  subscription() {
    this.router.navigate(['subscription']);
  }
}
