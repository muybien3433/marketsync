import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AssetSelectionListComponent } from './asset-selection-list.component';

describe('AssetSelectionListComponent', () => {
  let component: AssetSelectionListComponent;
  let fixture: ComponentFixture<AssetSelectionListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AssetSelectionListComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AssetSelectionListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
