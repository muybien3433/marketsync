//package pl.muybien.subscriptionservice.finance;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.math.BigDecimal;
//
//@RestController
//@RequestMapping("api/v1/finance")
//@RequiredArgsConstructor
//public class FinanceController {
//
//    private FinanceServiceFactory financeServiceFactory;
//
//    @GetMapping("/{uri}")
//    public BigDecimal findPrice(@PathVariable String uri) {
//        var service = financeServiceFactory.getService(uri);
//
//        String n = service.fetchCurrentFinance().ge
//        var financeProvider = financeProviderFactory.getProvider("crypto");
//
//        return financeProvider.fetchFinance(uri).getPriceUsd();
//    }
//}
