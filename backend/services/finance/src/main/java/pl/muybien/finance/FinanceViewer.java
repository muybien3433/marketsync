package pl.muybien.finance;

import org.springframework.stereotype.Service;
import pl.muybien.config.api.ApiProperties;
import pl.muybien.config.api.CryptoConfig;

import java.util.Map;

@Service
public class FinanceViewer {

    private final Map<String, CryptoConfig> cryptos;

    public FinanceViewer(ApiProperties apiProperties) {
        this.cryptos = apiProperties.getApi();
    }

    Map<String, CryptoConfig> displayAvailableCrypto() {
        return cryptos;
    }
}