import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SubscriptionAddComponent } from './subscription-add.component';

describe('AddSubscriptionComponent', () => {
  let component: SubscriptionAddComponent;
  let fixture: ComponentFixture<SubscriptionAddComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SubscriptionAddComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SubscriptionAddComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
