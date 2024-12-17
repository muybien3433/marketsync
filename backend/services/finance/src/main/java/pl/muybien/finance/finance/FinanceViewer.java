package pl.muybien.finance.finance;

import org.springframework.stereotype.Service;
import pl.muybien.finance.config.api.ApiProperties;
import pl.muybien.finance.config.api.CryptoConfig;

import java.util.Map;

@Service
public class FinanceViewer {

    private final Map<String, CryptoConfig> cryptos;

    public FinanceViewer(ApiProperties apiProperties) {
        this.cryptos = apiProperties.getApi();
    }

    public Map<String, CryptoConfig> displayAvailableCrypto() {
        return cryptos;
    }
}