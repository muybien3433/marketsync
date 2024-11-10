package pl.muybien.subscriptionservice.finance;

public interface FinanceProvider {
    Finance fetchFinance(String financeName);
}
