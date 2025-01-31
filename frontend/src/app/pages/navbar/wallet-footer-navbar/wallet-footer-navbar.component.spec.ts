import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WalletFooterNavbarComponent } from './wallet-footer-navbar.component';

describe('WalletFooterNavbarComponent', () => {
  let component: WalletFooterNavbarComponent;
  let fixture: ComponentFixture<WalletFooterNavbarComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [WalletFooterNavbarComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(WalletFooterNavbarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
