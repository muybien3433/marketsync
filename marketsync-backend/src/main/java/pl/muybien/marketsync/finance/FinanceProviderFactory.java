package pl.muybien.marketsync.finance;

public interface FinanceProviderFactory {
    FinanceProvider getProvider(String financeName);
}
