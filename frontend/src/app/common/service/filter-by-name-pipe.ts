import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  standalone: true,
  name: 'filterByName'
})
export class FilterByNamePipe implements PipeTransform {
  transform(assets: any[], searchTerm: string): any[] {
    if (!assets || !searchTerm) {
      return assets;
    }

    return assets.filter(asset =>
      (asset.name + ' ' + asset.symbol).toLowerCase().includes(searchTerm.toLowerCase())
    );
  }
}
