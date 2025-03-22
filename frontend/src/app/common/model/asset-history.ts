export class AssetHistory {
    constructor(
        public id: number,
        public name: string,
        public symbol: string,
        public count: number,
        public currencyType: string,
        public purchasePrice: number,
        public createdDate: string,
        public assetType: string,
    ) {
    }
}