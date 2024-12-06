import { Component } from '@angular/core';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import {Router, RouterOutlet} from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    TranslateModule,
    RouterOutlet
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})

export class AppComponent {
  constructor(private router: Router, private translate: TranslateService) {
    this.translate.addLangs(['pl', 'en']);
    this.translate.setDefaultLang('pl');
    this.translate.use('pl');
  }

  useLanguage(language: string): void {
    this.translate.use(language);
  }

  main() {
    this.router.navigate(['/']);
  }

  logIn() {
    this.router.navigate(['login']);
  }

  signUp() {
    this.router.navigate(['register']);
  }

  wallet() {
    this.router.navigate(['wallet']);
  }

  subscription() {
    this.router.navigate(['subscription']);
  }
}
