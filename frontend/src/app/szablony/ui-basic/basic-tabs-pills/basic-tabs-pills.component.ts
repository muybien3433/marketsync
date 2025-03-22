import { Component } from '@angular/core';
import {NgbNav, NgbNavItem, NgbNavOutlet} from "@ng-bootstrap/ng-bootstrap";

@Component({
  selector: 'app-basic-tabs-pills',
  standalone: true,
  imports: [
    NgbNavItem,
    NgbNav,
    NgbNavOutlet
  ],
  templateUrl: './basic-tabs-pills.component.html',
  styleUrls: ['./basic-tabs-pills.component.scss']
})
export default class BasicTabsPillsComponent {}
