package pl.muybien.asset;

public enum AssetType {
    UNDEFINED,
    CUSTOM,
    STOCKS,
    CRYPTOS,
    CURRENCIES,
    BONDS;

    public static AssetType fromString(String type) {
        if (type != null) {
            try {
                return AssetType.valueOf(type.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Asset assetType " + type + " not supported");
            }
        }
        throw new IllegalArgumentException("Asset assetType is null");
    }
}
