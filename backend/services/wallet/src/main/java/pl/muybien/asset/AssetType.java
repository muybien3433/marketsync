package pl.muybien.asset;

import lombok.ToString;

@ToString
public enum AssetType {
    UNDEFINED,
    STOCKS,
    BONDS,
    CRYPTOS,
    CURRENCIES;

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
