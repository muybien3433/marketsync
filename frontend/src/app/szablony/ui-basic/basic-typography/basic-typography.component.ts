import { Component } from '@angular/core';
import {CardComponent} from "../../../common/components/card/card.component";

@Component({
  selector: 'app-basic-typography',
  standalone: true,
  imports: [
    CardComponent
  ],
  templateUrl: './basic-typography.component.html',
  styleUrls: ['./basic-typography.component.scss']
})
export default class BasicTypographyComponent {}
