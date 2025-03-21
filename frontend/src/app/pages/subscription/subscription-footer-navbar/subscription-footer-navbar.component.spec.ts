import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SubscriptionFooterNavbarComponent } from './subscription-footer-navbar.component';

describe('FooterNavbarComponent', () => {
  let component: SubscriptionFooterNavbarComponent;
  let fixture: ComponentFixture<SubscriptionFooterNavbarComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SubscriptionFooterNavbarComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SubscriptionFooterNavbarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
