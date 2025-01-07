package pl.muybien.finance;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Slf4j
public class FinanceFileResolver {

    @Value("${coinmarketcap.file-name}")
    private String coinmarketcapFileName;

    Path resolvePathAndCheckDirectory(String type) {
        String fileName = fileNameResolver(type);
        Path classPath = Paths.get(FinanceFileManager.class
                .getProtectionDomain().getCodeSource().getLocation().getPath()).getParent();

        Path dataDirectoryPath = classPath.resolve("data");
        Path filePath = dataDirectoryPath.resolve(fileName);
        ensureDirectoryExists(dataDirectoryPath);

        return filePath;
    }

    private String fileNameResolver(String type) {
        switch (type.trim().toLowerCase()) {
            case "cryptos": return coinmarketcapFileName;
            case "stocks": return "";
        }
        log.error("Type not recognized: {}", type);
        return "";
    }

    private void ensureDirectoryExists(Path dataDirectoryPath) {
        if (Files.notExists(dataDirectoryPath)) {
            try {
                Files.createDirectories(dataDirectoryPath);
                log.info("Directory created at path: {}", dataDirectoryPath);
            } catch (IOException e) {
                log.error("Error creating directory at path: {}", dataDirectoryPath, e);
            }
        }
    }
}
