import { Component } from '@angular/core';
import {CardComponent} from "../../../common/components/card/card.component";
import {NgbDropdown} from "@ng-bootstrap/ng-bootstrap";

@Component({
  selector: 'app-basic-button',
  standalone: true,
  imports: [
    CardComponent,
    NgbDropdown
  ],
  templateUrl: './basic-button.component.html',
  styleUrls: ['./basic-button.component.scss']
})
export default class BasicButtonComponent {}
