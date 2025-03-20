import { Component } from '@angular/core';
import {TranslatePipe} from "@ngx-translate/core";
import {Router} from "@angular/router";

@Component({
  selector: 'app-footer-navbar',
  standalone: true,
    imports: [
        TranslatePipe
    ],
  templateUrl: './footer-navbar.component.html',
  styleUrl: './footer-navbar.component.css'
})
export class FooterNavbarComponent {

  constructor(
      private router: Router,
  ) {
  }

  backToSubscriptions() {
    this.router.navigate(['subscription']);
  }

  addSubscription() {
    this.router.navigate(['add-subscription']);
  }
}
