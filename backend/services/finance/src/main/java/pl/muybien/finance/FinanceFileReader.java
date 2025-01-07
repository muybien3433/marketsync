package pl.muybien.finance;

import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    private final FinanceFileManager financeFileManager;

    @Value("${coinmarketcap.file-name}")
    private String cryptoFileName;

    public List<FinanceFileDTO> displayAvailableCrypto(String type) {
        String fileName = fileNameResolver(type);
        Path filePath = financeFileManager.resolvePathAndCheckDirectory(fileName);

        return readFinanceFromFile(filePath);
    }

    private String fileNameResolver(String type) {
        switch (type.toLowerCase()) {
            case "cryptos": return cryptoFileName;
            case "stocks": return "stocks";
            default: return "";
        }
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
