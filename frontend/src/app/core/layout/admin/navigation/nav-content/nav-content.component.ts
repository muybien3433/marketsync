import { Component, inject, output } from '@angular/core';
import { Location } from '@angular/common';
import { NavigationItem, NavigationItems } from '../navigation';
import { NavGroupComponent } from './nav-group/nav-group.component';
import {NgScrollbar} from "ngx-scrollbar";

@Component({
  selector: 'app-nav-content',
  imports: [NavGroupComponent, NgScrollbar],
  templateUrl: './nav-content.component.html',
  styleUrls: ['./nav-content.component.scss']
})
export class NavContentComponent {
  private location = inject(Location);
  navigations!: NavigationItem[];
  NavCollapsedMob = output();

  constructor() {
    this.navigations = NavigationItems;
  }

  fireOutClick() {
    let current_url = this.location.path();
    if (this.location['_baseHref']) {
      current_url = this.location['_baseHref'] + this.location.path();
    }
    const link = "a.nav-link[ href='" + current_url + "' ]";
    const ele = document.querySelector(link);
    if (ele !== null && ele !== undefined) {
      const parent = ele.parentElement;
      const up_parent = parent.parentElement.parentElement;
      const last_parent = up_parent.parentElement;
      if (parent.classList.contains('pcoded-hasmenu')) {
        parent.classList.add('pcoded-trigger');
        parent.classList.add('active');
      } else if (up_parent.classList.contains('pcoded-hasmenu')) {
        up_parent.classList.add('pcoded-trigger');
        up_parent.classList.add('active');
      } else if (last_parent.classList.contains('pcoded-hasmenu')) {
        last_parent.classList.add('pcoded-trigger');
        last_parent.classList.add('active');
      }
    }
  }
}
