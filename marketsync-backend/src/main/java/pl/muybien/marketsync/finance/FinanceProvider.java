package pl.muybien.marketsync.finance;

public interface FinanceProvider {
    Finance fetchFinance(String financeName);
}
