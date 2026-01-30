import { Component } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {DatePipe, NgForOf, NgIf} from "@angular/common";
import {TranslatePipe} from "@ngx-translate/core";
import {Router} from "@angular/router";
import {API_ENDPOINTS} from "../../../../core/http/api/api-endpoints";
import {CardComponent} from "../../../../shared/ui/card/card.component";
import { CurrencyType } from 'src/app/shared/enum/currency-type';
import {SubscriptionDetail} from "../../models/subscription-detail.model";

@Component({
  selector: 'app-subscription-list',
  standalone: true,
  imports: [
    NgForOf,
    NgIf,
    TranslatePipe,
    DatePipe,
    CardComponent
  ],
  templateUrl: './subscription-list.component.html',
  styleUrl: './subscription-list.component.scss'
})
export default class SubscriptionListComponent {
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
    this.http.get<SubscriptionDetail[]>(`${API_ENDPOINTS.SUBSCRIPTION}`).subscribe({
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
    this.http.delete(`${API_ENDPOINTS.SUBSCRIPTION}/${uri}/${id}`).subscribe({
      next: () => {
        this._subscriptions = this._subscriptions.filter(subscription => subscription.id !== id);
      },
      error: (err) => {
        console.error(err);
      }
    })
  }
}
