import { Component, Input, output } from '@angular/core';
import { RouterModule } from '@angular/router';
import {NgClass} from "@angular/common";

@Component({
  selector: 'app-nav-logo',
  imports: [RouterModule, NgClass],
  templateUrl: './nav-logo.component.html',
  styleUrls: ['./nav-logo.component.scss']
})
export class NavLogoComponent {
  @Input() navCollapsed: boolean;
  NavCollapse = output();
  windowWidth = window.innerWidth;

  navCollapse() {
    if (this.windowWidth >= 992) {
      this.navCollapsed = !this.navCollapsed;
      this.NavCollapse.emit();
    }
  }
}
