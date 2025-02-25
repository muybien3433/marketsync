package pl.muybien.finance.stock;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.muybien.finance.*;

@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private final FinanceRepository repository;

    @Override
    public FinanceResponse fetchStock(String uri, AssetType assetType) {
        if (uri == null || uri.isBlank()) {
            throw new IllegalArgumentException("Crypto identifier cannot be null or blank");
        }

        var finance = repository.findFinanceByAssetTypeIgnoreCase(assetType.name())
                .orElseThrow(() -> new IllegalArgumentException("Finance not found for asset type: " + assetType));

        var financeDetail = finance.getFinanceDetails()
                .stream()
                .filter(detail -> uri.equalsIgnoreCase(detail.getUri()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No finance details available for asset type: " + assetType));

        return new FinanceResponse(
                financeDetail.getName(),
                financeDetail.getSymbol(),
                financeDetail.getPrice(),
                financeDetail.getCurrency(),
                financeDetail.getAssetType());

    }
}
