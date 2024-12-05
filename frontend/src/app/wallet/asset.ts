export class Asset {

  constructor(public type: string,
              public name: string,
              public value: number,
              public averagePurchasePrice: number,
              public currentPrice: number,
              public investmentStartDate: number,
              public profitInPercentage: number,
              public profit: number) {
  }
}
