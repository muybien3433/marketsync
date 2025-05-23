import { Component, OnInit, inject, input } from '@angular/core';
import { Location } from '@angular/common';
import { NavigationItem } from '../../navigation';
import { NavItemComponent } from '../nav-item/nav-item.component';
import { NavCollapseComponent } from '../nav-collapse/nav-collapse.component';
import {TranslatePipe} from "@ngx-translate/core";

@Component({
  selector: 'app-nav-group',
  imports: [NavItemComponent, NavCollapseComponent, TranslatePipe],
  templateUrl: './nav-group.component.html',
  styleUrls: ['./nav-group.component.scss']
})
export class NavGroupComponent implements OnInit {
  private location = inject(Location);

  readonly item = input<NavigationItem>(undefined);

  ngOnInit() {
    let current_url = this.location.path();
    if (this.location['_baseHref']) {
      current_url = this.location['_baseHref'] + this.location.path();
    }
    const link = "a.nav-link[ href='" + current_url + "' ]";
    const ele = document.querySelector(link);
    if (ele !== null && ele !== undefined) {
      const parent = ele.parentElement;
      const up_parent = parent.parentElement.parentElement;
      const pre_parent = up_parent.parentElement;
      const last_parent = up_parent.parentElement.parentElement.parentElement.parentElement;
      if (parent.classList.contains('pcoded-hasmenu')) {
        parent.classList.add('pcoded-trigger');
        parent.classList.add('active');
      } else if (up_parent.classList.contains('pcoded-hasmenu')) {
        up_parent.classList.add('pcoded-trigger');
        up_parent.classList.add('active');
      } else if (pre_parent.classList.contains('pcoded-hasmenu')) {
        pre_parent.classList.add('pcoded-trigger');
        pre_parent.classList.add('active');
      }

      if (last_parent.classList.contains('pcoded-hasmenu')) {
        last_parent.classList.add('pcoded-trigger');

        if (pre_parent.classList.contains('pcoded-hasmenu')) {
          pre_parent.classList.add('pcoded-trigger');
        }
        last_parent.classList.add('active');
      }
    }
  }
}
