import { Component } from '@angular/core';
import {SubscriptionDetail} from "../../models/subscription-detail";
import {environment} from "../../../environments/environment";
import {API_ENDPOINTS} from "../../services/api-endpoints";
import {HttpClient} from "@angular/common/http";
import {CurrencyPipe, NgForOf, NgIf} from "@angular/common";
import {TranslatePipe} from "@ngx-translate/core";
import {Router} from "@angular/router";
import {FooterNavbarComponent} from "./footer-navbar/footer-navbar.component";

@Component({
  selector: 'app-subscription',
  standalone: true,
  imports: [
    FooterNavbarComponent,
    CurrencyPipe,
    NgForOf,
    NgIf,
    TranslatePipe
  ],
  templateUrl: './subscription.component.html',
  styleUrl: './subscription.component.css'
})
export class SubscriptionComponent {
  protected _subscriptions: SubscriptionDetail[] = [];
  isLoading: boolean = false;

  constructor(
      private http: HttpClient,
      private router: Router,
  ) {
  }

  fetchSubscriptions() {
    this.isLoading = true;
    this.http.get<SubscriptionDetail[]>(`${environment.baseUrl}${API_ENDPOINTS.SUBSCRIPTION}`).subscribe({
      next: (subscriptions) => {
        this._subscriptions = Array.isArray(subscriptions) ? subscriptions : [];
        this.isLoading = false;
      },
      error: (err) => {
        console.error(err);
        this._subscriptions = [];
        this.isLoading = false;
      },
    });
  }

  protected readonly Object = Object;

  addSubscription() {
    this.router.navigate(['subscriptions']);
  }
}
