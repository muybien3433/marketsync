package pl.muybien.finance;

import org.springframework.stereotype.Component;
import pl.muybien.entity.helper.FinanceDetail;
import pl.muybien.enumeration.CurrencyType;
import pl.muybien.finance.dto.FinanceBaseDTO;
import pl.muybien.finance.dto.FinanceDetailDTO;
import pl.muybien.response.FinanceResponse;

import java.math.BigDecimal;

import static pl.muybien.util.PriceUtil.normalizePrice;

@Component
public class FinanceDTOMapper {
    public FinanceDetailDTO toDetailDTO(FinanceDetail financeDetail) {
        return new FinanceDetailDTO(
                financeDetail.name(),
                financeDetail.symbol(),
                financeDetail.uri(),
                financeDetail.unitType(),
                normalizePrice(financeDetail.price()),
                financeDetail.currencyType(),
                financeDetail.assetType(),
                financeDetail.lastUpdated()
        );
    }

    public FinanceBaseDTO toBaseDTO(FinanceDetail financeDetail) {
        return new FinanceBaseDTO(
                financeDetail.name(),
                financeDetail.symbol(),
                financeDetail.uri(),
                financeDetail.unitType(),
                financeDetail.assetType()
        );
    }

    public FinanceResponse toResponse(FinanceDetail financeDetail) {
        return new FinanceResponse(
                financeDetail.name(),
                financeDetail.symbol(),
                financeDetail.uri(),
                financeDetail.unitType(),
                normalizePrice(financeDetail.price()),
                financeDetail.currencyType(),
                financeDetail.assetType(),
                financeDetail.lastUpdated()
        );
    }

    public FinanceResponse toResponseWithPriceAndCurrencyType(FinanceDetail financeDetail, BigDecimal price, CurrencyType currencyType) {
        return new FinanceResponse(
                financeDetail.name(),
                financeDetail.symbol(),
                financeDetail.uri(),
                financeDetail.unitType(),
                normalizePrice(price),
                currencyType,
                financeDetail.assetType(),
                financeDetail.lastUpdated()
        );
    }
}