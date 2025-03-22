export class AssetAggregate {
    constructor(
        public name: string,
        public symbol: string,
        public assetType: string,
        public count: number,
        public currentPrice: number,
        public currencyType: string,
        public value: number,
        public averagePurchasePrice: number,
        public profit: number,
        public profitInPercentage: number,
        public exchangeRateToDesired: number
    ) {
    }
}