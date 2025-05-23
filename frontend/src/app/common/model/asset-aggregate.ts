export class AssetAggregate {
    constructor(
        public name: string,
        public symbol: string,
        public assetType: string,
        public unitType: string,
        public count: number,
        public currentPrice: string,
        public currencyType: string,
        public value: number,
        public averagePurchasePrice: number,
        public profit: number,
        public profitInPercentage: number,
        public exchangeRateToDesired: number
    ) {
    }
}