package pl.muybien.finance;

public interface FinanceServiceFactory {
    FinanceService getService(String currencyName);
}
