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
  isMenuOpen = false;

  constructor(private translate: TranslateService) {
    this.translate.addLangs(['pl', 'en']);
    this.translate.setDefaultLang('pl');
    this.translate.use('pl');
  }

  getCurrentFlag(): string {
    return this.translate.currentLang === 'en' ? '🇬🇧' : '🇵🇱';
  }

  toggleLanguageMenu() {
    this.isMenuOpen = !this.isMenuOpen;
  }

  useLanguage(lang: string) {
    this.translate.use(lang);
    this.isMenuOpen = false;
  }

  @HostListener('document:click', ['$event'])
  onClick(event: MouseEvent) {
    const target = event.target as HTMLElement;
    if (!target.closest('.language-picker')) {
      this.isMenuOpen = false;
    }
  }
}