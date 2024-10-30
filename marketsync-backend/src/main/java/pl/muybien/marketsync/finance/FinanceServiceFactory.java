package pl.muybien.marketsync.finance;

public interface FinanceServiceFactory {
    FinanceService getService(String currencyName);
}
