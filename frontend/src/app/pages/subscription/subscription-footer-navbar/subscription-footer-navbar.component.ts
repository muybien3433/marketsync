import { Component } from '@angular/core';
import {TranslatePipe} from "@ngx-translate/core";
import {Router} from "@angular/router";

@Component({
  selector: 'app-subscription-footer-navbar',
  standalone: true,
    imports: [
        TranslatePipe
    ],
  templateUrl: './subscription-footer-navbar.component.html',
  styleUrl: './subscription-footer-navbar.component.css'
})
export class SubscriptionFooterNavbarComponent {

  constructor(
      private router: Router,
  ) {
  }

  backToSubscriptions() {
    this.router.navigate(['subscription']);
  }

  addSubscription() {
    this.router.navigate(['subscription-add']);
  }
}
