export class AssetHistoryModel {
  constructor(
    public id: number,
    public type: string,
    public name: string,
    public count: number,
    public createdDate: number,
    public purchasePrice: number
  ) {
  }
}
