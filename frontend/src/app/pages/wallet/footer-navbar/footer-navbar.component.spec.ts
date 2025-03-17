import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FooterNavbarComponent } from './footer-navbar.component';

describe('WalletFooterNavbarComponent', () => {
  let component: FooterNavbarComponent;
  let fixture: ComponentFixture<FooterNavbarComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FooterNavbarComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FooterNavbarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
