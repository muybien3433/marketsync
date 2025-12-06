export class AssetHistory {
    constructor(
        public id: number,
        public name: string,
        public uri: string,
        public symbol: string,
        public count: number,
        public currencyType: string,
        public purchasePrice: number,
        public currentPrice: number,
        public createdDate: string,
        public assetType: string,
        public unitType: string,
        public comment: string,
    ) {
    }
}