import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AssetSelectionComponent } from './asset-selection.component';

describe('AssetSelectionComponent', () => {
  let component: AssetSelectionComponent;
  let fixture: ComponentFixture<AssetSelectionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AssetSelectionComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AssetSelectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
