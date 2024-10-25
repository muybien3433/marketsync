package pl.muybien.marketsync.asset;

public interface AssetProviderFactory {
    AssetProvider getProvider(String uri);
}
