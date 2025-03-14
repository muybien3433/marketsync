import { Component } from '@angular/core';
import {Router} from "@angular/router";
import {NgIf} from "@angular/common";
import {TranslatePipe} from "@ngx-translate/core";
import {KeycloakService} from "keycloak-angular";

@Component({
  selector: 'app-hamburger-menu',
  standalone: true,
  imports: [
    NgIf,
    TranslatePipe
  ],
  templateUrl: './hamburger-menu.component.html',
  styleUrl: './hamburger-menu.component.css'
})
export class HamburgerMenuComponent {
  isMenuOpen = false;

  constructor(private router: Router, private keycloakService: KeycloakService) {
  }

  toggleMenu() {
    this.isMenuOpen = !this.isMenuOpen;
  }

  navigateTo(page: string) {
    this.router.navigate([`/${page}`]);
    this.isMenuOpen = false;
  }

  logout(): void {
    this.keycloakService.logout();
  }
}