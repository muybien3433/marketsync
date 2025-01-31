export class AssetAggregate {
    constructor(
        public name: string,
        public symbol: string,
        public assetType: string,
        public count: number,
        public currentPrice: number,
        public currency: string,
        public value: number,
        public averagePurchasePrice: number,
        public profitInPercentage: number,
        public profit: number,
        public exchangeRateToDesired: number
    ) {
    }
}