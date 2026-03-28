package vkr.verifier.util;

import java.util.Locale;

import org.springframework.web.multipart.MultipartFile;

import vkr.verifier.exception.InvalidFileException;

public class FormatFileValidator {
    private final static String FILE_CORRECT_MIME_TYPE =
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document";

    public static void validateDocx(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidFileException("Файл пустой или не был передан");
        }
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.toLowerCase(Locale.ROOT).endsWith(".docx")) {
            throw new InvalidFileException("Допустим только файл формата .docx");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.equals(FILE_CORRECT_MIME_TYPE)) {
            throw new InvalidFileException("Некорректный MIME-тип файла");
        }
    }
}
