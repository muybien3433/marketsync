package pl.muybien.finance.finance;

public interface FinanceServiceFactory {
    FinanceService getService(String currencyName);
}
