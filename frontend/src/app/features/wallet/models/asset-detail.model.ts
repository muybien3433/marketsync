export class AssetDetail {
    constructor(
        public name: string,
        public symbol: string,
        public uri: string,
        public price: number,
        public currencyType: string,
        public assetType: string,
        public unitType: string,
        public comment: string,
        public lastUpdated: string
    ) {
    }
}