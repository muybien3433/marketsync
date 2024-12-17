package pl.muybien.wallet.asset;

import lombok.ToString;

@ToString
public enum AssetType {
    undefined,
    stock,
    bonds,
    crypto,
    currency
}
