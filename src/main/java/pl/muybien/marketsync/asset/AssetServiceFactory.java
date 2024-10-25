package pl.muybien.marketsync.asset;

public interface AssetServiceFactory {
    AssetService getService(String currencyName);
}
