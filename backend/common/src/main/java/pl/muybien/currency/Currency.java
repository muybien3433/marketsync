package pl.muybien.currency;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Document(collection = "currency_exchange")
public class Currency {

    @Id
    private String name;
    private BigDecimal exchange;
    private String unitType;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    public Currency() {
    }

    public Currency(String name, BigDecimal exchange, String unitType, LocalDateTime lastModifiedDate) {
        this.name = name;
        this.exchange = exchange;
        this.unitType = unitType;
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getExchange() {
        return exchange;
    }

    public void setExchange(BigDecimal exchange) {
        this.exchange = exchange;
    }

    public String getUnitType() {
        return unitType;
    }

    public void setUnitType(String unitType) {
        this.unitType = unitType;
    }

    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
}