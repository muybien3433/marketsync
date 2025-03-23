import {Component, inject, OnDestroy, OnInit} from '@angular/core';
import {NgbDropdown, NgbDropdownConfig, NgbDropdownMenu, NgbDropdownToggle} from '@ng-bootstrap/ng-bootstrap';
import {NgClass} from "@angular/common";
import screenfull from "screenfull";
import {KeycloakService} from "keycloak-angular";

@Component({
  selector: 'app-nav-right',
  imports: [
    NgbDropdownToggle,
    NgbDropdownMenu,
    NgbDropdown,
    NgClass
  ],
  templateUrl: './nav-right.component.html',
  styleUrls: ['./nav-right.component.scss'],
  providers: [NgbDropdownConfig]
})
export class NavRightComponent implements OnInit, OnDestroy {
  screenFull = true;

  constructor(private keycloakService: KeycloakService) {
    const config = inject(NgbDropdownConfig);

    config.placement = 'bottom-right';
  }

  ngOnInit() {
    if (screenfull.isEnabled) {
      this.screenFull = screenfull.isFullscreen;
      screenfull.on('change', () => {
        this.screenFull = screenfull.isFullscreen;
      });
    }
  }

  ngOnDestroy() {
    if (screenfull.isEnabled) {
      screenfull.off('change', () => {
        this.screenFull = screenfull.isFullscreen;
      });
    }
  }

  toggleFullscreen() {
    if (screenfull.isEnabled) {
      screenfull.toggle().then(() => {
        this.screenFull = screenfull.isFullscreen;
      });
    }
  }

  logout() {
    this.keycloakService.logout();
  }
}
