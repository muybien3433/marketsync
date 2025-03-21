import { Component } from '@angular/core';
import {SubscriptionDetail} from "../../models/subscription-detail";
import {environment} from "../../../environments/environment";
import {API_ENDPOINTS} from "../../services/api-endpoints";
import {HttpClient} from "@angular/common/http";
import {DatePipe, NgForOf, NgIf} from "@angular/common";
import {TranslatePipe} from "@ngx-translate/core";
import {Router} from "@angular/router";
import {SubscriptionFooterNavbarComponent} from "./subscription-footer-navbar/subscription-footer-navbar.component";

@Component({
  selector: 'app-subscription',
  standalone: true,
  imports: [
    SubscriptionFooterNavbarComponent,
    NgForOf,
    NgIf,
    TranslatePipe,
    DatePipe
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
    this.fetchSubscriptions();
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
    this.router.navigate(['subscription-add']);
  }

  deleteSubscription(uri: string, id: string) {
    this.http.delete(`${environment.baseUrl}${API_ENDPOINTS.SUBSCRIPTION}/${uri}/${id}`).subscribe({
      next: () => {
        this._subscriptions = this._subscriptions.filter(subscription => subscription.id !== id);
      },
      error: (err) => {
        console.error(err);
      }
    })
  }
}
