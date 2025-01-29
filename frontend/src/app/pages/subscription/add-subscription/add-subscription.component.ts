import { Component } from '@angular/core';
import {AssetSelectionComponent} from '../../asset-selection-list/asset-selection/asset-selection.component';
import {TranslatePipe} from '@ngx-translate/core';

@Component({
  selector: 'app-add-subscription',
  standalone: true,
  imports: [
    AssetSelectionComponent,
    TranslatePipe
  ],
  templateUrl: './add-subscription.component.html',
  styleUrl: './add-subscription.component.css'
})
export class AddSubscriptionComponent {

}
