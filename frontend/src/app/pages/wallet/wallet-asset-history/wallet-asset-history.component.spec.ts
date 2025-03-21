import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WalletAssetHistoryComponent } from './wallet-asset-history.component';

describe('AssetHistoryComponent', () => {
  let component: WalletAssetHistoryComponent;
  let fixture: ComponentFixture<WalletAssetHistoryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [WalletAssetHistoryComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(WalletAssetHistoryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
