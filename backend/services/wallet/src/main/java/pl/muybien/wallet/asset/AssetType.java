package pl.muybien.wallet.asset;

import lombok.ToString;

@ToString
public enum AssetType {
    UNDEFINED,
    STOCK,
    BONDS,
    CRYPTO,
    CURRENCY
}
