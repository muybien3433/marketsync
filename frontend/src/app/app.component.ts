import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {WalletComponent } from './wallet/wallet.component';
import {TranslateModule, TranslateService} from '@ngx-translate/core';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    RouterOutlet,
    WalletComponent,
    TranslateModule
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})

export class AppComponent {
  constructor(private translate: TranslateService) {
    this.translate.addLangs(['pl', 'en']);
    this.translate.setDefaultLang('pl');
    this.translate.use('pl');
  }

  useLanguage(language: string): void {
    this.translate.use(language);
  }

  logIn() {

  }

  signUp() {

  }

  wallet() {

  }
}
