export class Asset {
  constructor(
    public id: number,
    public name: string,
    public assetType: string,
    public count: number,
    public currentPrice: number,
    public requestedCurrency: string,
    public currency: string,
    public value: number,
    public averagePurchasePrice: number,
    public profit: number,
    public profitInPercentage: number,
  ) {
  }
}
