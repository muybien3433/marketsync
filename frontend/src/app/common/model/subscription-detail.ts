export class SubscriptionDetail {
    constructor(
        public id: string,
        public financeName: string,
        public uri: string,
        public upperBoundPrice: number,
        public lowerBoundPrice: number,
        public assetType: string,
        public notificationType: string,
        public createdDate: string
    ) {
    }
}
