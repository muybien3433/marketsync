import {Component, HostListener} from '@angular/core';
import {TranslateService} from "@ngx-translate/core";

@Component({
  selector: 'app-language-menu',
  standalone: true,
  imports: [],
  templateUrl: './language-menu.component.html',
  styleUrl: './language-menu.component.css'
})
export class LanguageMenuComponent {
  menuOpen = false;

  constructor(private translate: TranslateService) {
    this.translate.addLangs(['pl', 'en']);
    this.translate.setDefaultLang('pl');
    this.translate.use('pl');
  }

  getCurrentFlag(): string {
    return this.translate.currentLang === 'en' ? '🇬🇧' : '🇵🇱';
  }

  toggleLanguageMenu() {
    this.menuOpen = !this.menuOpen;
  }

  useLanguage(lang: string) {
    this.translate.use(lang);
    this.menuOpen = false;
  }

  @HostListener('document:click', ['$event'])
  onClick(event: MouseEvent) {
    const target = event.target as HTMLElement;
    if (!target.closest('.language-picker')) {
      this.menuOpen = false;
    }
  }
}