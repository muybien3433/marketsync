import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CurrencyPreferenceChangeComponent } from './currency-preference-change.component';

describe('CurrencyComponent', () => {
  let component: CurrencyPreferenceChangeComponent;
  let fixture: ComponentFixture<CurrencyPreferenceChangeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CurrencyPreferenceChangeComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CurrencyPreferenceChangeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
