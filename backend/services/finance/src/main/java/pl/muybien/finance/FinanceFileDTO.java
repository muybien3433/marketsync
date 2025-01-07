package pl.muybien.finance;

import com.opencsv.bean.CsvBindByPosition;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class FinanceFileDTO {
    @CsvBindByPosition(position = 0)
    String name;

    @CsvBindByPosition(position = 1)
    String symbol;

    @CsvBindByPosition(position = 2)
    String uri;
}
