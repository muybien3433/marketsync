import {Injectable} from '@angular/core';
import {BehaviorSubject} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AssetService {
  private assetUriSource = new BehaviorSubject<string>('');
  assetUri$ = this.assetUriSource.asObservable();

  setAssetUri(uri: string): void {
    this.assetUriSource.next(uri);
  }
}
