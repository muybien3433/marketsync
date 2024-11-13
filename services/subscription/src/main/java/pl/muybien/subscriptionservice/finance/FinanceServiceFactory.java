package pl.muybien.subscriptionservice.finance;

public interface FinanceServiceFactory {
    FinanceService getService(String currencyName);
}
