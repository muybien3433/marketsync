import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WalletAddAssetComponent } from './wallet-add-asset.component';

describe('AddAssetComponent', () => {
  let component: WalletAddAssetComponent;
  let fixture: ComponentFixture<WalletAddAssetComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [WalletAddAssetComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(WalletAddAssetComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
