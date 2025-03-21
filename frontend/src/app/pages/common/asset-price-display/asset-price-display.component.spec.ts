import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AssetPriceDisplayComponent } from './asset-price-display.component';

describe('AssetPriceDisplayerComponent', () => {
  let component: AssetPriceDisplayComponent;
  let fixture: ComponentFixture<AssetPriceDisplayComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AssetPriceDisplayComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AssetPriceDisplayComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
