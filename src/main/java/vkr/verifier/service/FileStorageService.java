package vkr.verifier.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import vkr.verifier.config.properties.StorageProperties;

@Service
@RequiredArgsConstructor
public class FileStorageService {
    private final StorageProperties storageProperties;

    public Path getBaseDir() {
        return Path.of(System.getProperty("user.dir"));
    }

    public Path getStorageDir() {
        return getBaseDir()
            .resolve(storageProperties.getUploadsDir())
            .normalize();
    }

    public void save(MultipartFile file, String storedFilename) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Файл пустой");
        }
        if (storedFilename == null || storedFilename.isBlank()) {
            throw new IllegalArgumentException("Имя файла для сохранения не задано");
        }
        try {
            Path storageDir = getStorageDir();
            Files.createDirectories(storageDir);

            Path targetPath = storageDir.resolve(storedFilename).normalize();

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new RuntimeException("Не удалось сохранить файл: " + storedFilename, e);
        }
    }

    public void delete(String storedFilename) {
        if (storedFilename == null || storedFilename.isBlank()) {
            throw new IllegalArgumentException("Имя файла для удаления не задано");
        }
        try {
            Path filePath = getStorageDir().resolve(storedFilename).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось удалить файл: " + storedFilename, e);
        }
    }

    public Path getFilePath(String storedFilename) {
        if (storedFilename == null || storedFilename.isBlank()) {
            throw new IllegalArgumentException("Имя файла не задано");
        }
        return getStorageDir().resolve(storedFilename).normalize();
    }
}
