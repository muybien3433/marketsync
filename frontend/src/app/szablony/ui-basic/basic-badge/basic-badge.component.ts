import { Component } from '@angular/core';
import {CardComponent} from "../../../common/components/card/card.component";

@Component({
  selector: 'app-basic-badge',
  standalone: true,
  imports: [
    CardComponent
  ],
  templateUrl: './basic-badge.component.html',
  styleUrls: ['./basic-badge.component.scss']
})
export default class BasicBadgeComponent {}
