import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WalletEditAssetComponent } from './wallet-edit-asset.component';

describe('EditAssetComponent', () => {
  let component: WalletEditAssetComponent;
  let fixture: ComponentFixture<WalletEditAssetComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [WalletEditAssetComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(WalletEditAssetComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
