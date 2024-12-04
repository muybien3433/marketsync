package pl.muybien.subscription.finance;

public interface FinanceServiceFactory {
    FinanceService getService(String currencyName);
}
