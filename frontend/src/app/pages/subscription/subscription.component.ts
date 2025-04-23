import { Component } from '@angular/core';
import {environment} from "../../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {DatePipe, NgForOf, NgIf} from "@angular/common";
import {TranslatePipe} from "@ngx-translate/core";
import {Router} from "@angular/router";
import {SubscriptionDetail} from "../../common/model/subscription-detail";
import {API_ENDPOINTS} from "../../common/service/api-endpoints";
import {CardComponent} from "../../common/components/card/card.component";
import { CurrencyType } from 'src/app/common/model/currency-type';

@Component({
  selector: 'app-subscription',
  standalone: true,
  imports: [
    NgForOf,
    NgIf,
    TranslatePipe,
    DatePipe,
    CardComponent
  ],
  templateUrl: './subscription.component.html',
  styleUrl: './subscription.component.scss'
})
export default class SubscriptionComponent {
  protected _subscriptions: SubscriptionDetail[] = [];
  isLoading: boolean = true;
  CurrencyType = CurrencyType;

  constructor(
      private http: HttpClient,
      private router: Router,
  ) {
    this.fetchSubscriptions();
  }

  protected readonly Object = Object;

  addSubscription() {
    this.router.navigate(['subscription/add']);
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
