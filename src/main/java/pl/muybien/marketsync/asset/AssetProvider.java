package pl.muybien.marketsync.asset;

public interface AssetProvider {
    Asset fetchAsset(String assetName);
}
