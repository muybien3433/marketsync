package pl.muybien.finance;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashSet;

@Service
@Slf4j
public class FinanceFileManager {

    private static final int STALE_THRESHOLD_MINUTES = 1440;

    public void writeDataToFile(LinkedHashSet<FinanceResponse> sortedAssets, String fileName) {
        Path filePath = resolvePathAndCheckDirectory(fileName);

        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            log.info("Writing data to file...: {}", filePath);
            for (var asset : sortedAssets) {
                writer.write(asset.name() + "," + asset.symbol() + "," + asset.uri());
                writer.newLine();
            }
            log.info("Data written successfully to file: {}", filePath);
        } catch (IOException e) {
            log.error("Error writing data to file {}", filePath, e);
        }
    }

    private Path resolvePathAndCheckDirectory(String fileName) {
        Path classPath = Paths.get(FinanceFileManager.class
                .getProtectionDomain().getCodeSource().getLocation().getPath()).getParent();

        Path dataDirectoryPath = classPath.resolve("data");
        Path filePath = dataDirectoryPath.resolve(fileName);
        ensureDirectoryExists(dataDirectoryPath);

        return filePath;
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

    public boolean isUpdateRequired(String fileName) {
        Path filePath = resolvePathAndCheckDirectory(fileName);
        if (Files.notExists(filePath)) {
            log.info("File {} not found, start creating new one...", filePath);
            return true;
        } else if (Files.exists(filePath)) {
            try {
                log.info("Checking whether the update is needed: {}", filePath);
                FileTime fileTime = Files.getLastModifiedTime(filePath);
                Instant lastModified = fileTime.toInstant();
                Instant now = Instant.now();
                Duration duration = Duration.between(lastModified, now);

                if (duration.toMinutes() >= STALE_THRESHOLD_MINUTES) {
                    log.info("Starting to update file...: {}", filePath);
                    return true;
                }
            } catch (IOException e) {
                log.error("Error checking if file update is needed: {}", filePath, e);
                return false;
            }
        }
        log.info("Already up to date: {}", filePath);
        return false;
    }
}
