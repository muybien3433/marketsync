package pl.muybien.enumeration;

public enum AssetType {
    CUSTOM,
    STOCK,
    CRYPTO,
    CURRENCY,
    COMMODITY,
    ETF,
    BOND;

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
