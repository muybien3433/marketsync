export class Asset {
  constructor(
    public id: number,
    public type: string,
    public name: string,
    public count: number,
    public value: number,
    public averagePurchasePrice: number,
    public currentPrice: number,
    public investmentStartDate: number,
    public profitInPercentage: number,
    public profit: number
  ) {
  }
}
