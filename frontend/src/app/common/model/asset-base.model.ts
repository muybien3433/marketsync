export class AssetBase {
    constructor(
        public name: string,
        public symbol: string,
        public uri: string,
        public assetType: string,
        public unitType: string,
    ) {
    }
}