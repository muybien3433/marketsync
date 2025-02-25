package pl.muybien.finance;

import org.springframework.stereotype.Service;

@Service
public class FinanceDetailDTOMapper {
    public FinanceDetailDTO toDTO(FinanceDetail financeDetail) {
        return new FinanceDetailDTO(
                financeDetail.getName(),
                financeDetail.getSymbol(),
                financeDetail.getUri(),
                financeDetail.getPrice(),
                financeDetail.getCurrency(),
                financeDetail.getAssetType()
        );
    }
}