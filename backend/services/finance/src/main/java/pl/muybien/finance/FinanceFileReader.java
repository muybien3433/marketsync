package pl.muybien.finance;

import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FinanceFileReader {

    private final FinanceFileResolver financeFileResolver;

    public List<FinanceFileDTO> displayAvailableFinance(String type) {
        Path filePath = financeFileResolver.resolvePathAndCheckDirectory(type);

        return readFinanceFromFile(filePath);
    }

    private List<FinanceFileDTO> readFinanceFromFile(Path filePath) {
        log.info("Reading from file: {}", filePath);
        try (Reader reader = new FileReader(filePath.toFile())) {
            var csvToBean = new CsvToBeanBuilder<FinanceFileDTO>(reader)
                    .withType(FinanceFileDTO.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            return csvToBean.parse();
        } catch (IOException e) {
            log.error("Could not read data from file {}", filePath, e);
            return Collections.emptyList();
        }
    }
}
