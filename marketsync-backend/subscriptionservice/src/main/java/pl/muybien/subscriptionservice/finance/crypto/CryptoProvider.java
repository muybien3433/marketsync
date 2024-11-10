//package pl.muybien.subscriptionservice.finance.crypto;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.web.reactive.function.client.WebClient;
//import pl.muybien.subscriptionservice.finance.FinanceProvider;
//import pl.muybien.subscriptionservice.handler.FinanceNotFoundException;
//
//@Service("crypto")
//@RequiredArgsConstructor
//public class CryptoProvider implements FinanceProvider {
//
//    private final WebClient webClient;
//    private final CryptoMapper cryptoMapper;
//
//    public Crypto fetchFinance(String financeUri) {
//        Crypto currentCrypto = webClient.get()
//                .uri(financeUri)
//                .retrieve()
//                .bodyToMono(CryptoResponse.class)
//                .map(cryptoMapper::mapToCrypto)
//                .block();
//        if (currentCrypto == null) {
//            throw new FinanceNotFoundException("Crypto data not found for URI: %s".formatted(financeName));
//        }
//        return currentCrypto;
//    }
//}
