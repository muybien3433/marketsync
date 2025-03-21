import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CurrencyChangeOptionComponent } from './currency-change-option.component';

describe('CurrencyChangeOptionComponent', () => {
  let component: CurrencyChangeOptionComponent;
  let fixture: ComponentFixture<CurrencyChangeOptionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CurrencyChangeOptionComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CurrencyChangeOptionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
