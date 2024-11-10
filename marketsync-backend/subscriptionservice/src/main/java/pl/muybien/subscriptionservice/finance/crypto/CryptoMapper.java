//package pl.muybien.subscriptionservice.finance.crypto;
//
//import org.springframework.stereotype.Service;
//
//@Service
//public class CryptoMapper {
//    Crypto mapToCrypto(CryptoResponse response) {
//
//        if (response.data() == null) {
//            throw new IllegalArgumentException("Crypto data is null");
//        }
//        Crypto crypto = response.data();
//
//        return Crypto.builder()
//                .symbol(crypto.getSymbol())
//                .name(crypto.getName())
//                .priceUsd(crypto.getPriceUsd())
//                .build();
//    }
//}