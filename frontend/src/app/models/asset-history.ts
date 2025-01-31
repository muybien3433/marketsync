export class AssetHistory {
    constructor(
        public id: number,
        public name: string,
        public symbol: string,
        public count: number,
        public currency: string,
        public purchasePrice: number,
        public createdDate: number,
        public assetType: string,
    ) {
    }
}