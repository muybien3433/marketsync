package pl.muybien.finance;

import org.springframework.stereotype.Service;

@Service
public class FinanceDetailDTOMapper {
    public FinanceDetailDTO toDTO(FinanceDetail financeDetail) {
        return new FinanceDetailDTO(
                financeDetail.name(),
                financeDetail.symbol(),
                financeDetail.uri(),
                financeDetail.unitType(),
                financeDetail.price(),
                financeDetail.currencyType(),
                financeDetail.assetType(),
                financeDetail.lastUpdated()
        );
    }
}