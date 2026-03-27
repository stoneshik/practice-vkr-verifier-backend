package vkr.verifier.service;

import java.util.Locale;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import vkr.verifier.dto.response.ReportGeneralInfoResponseDto;
import vkr.verifier.exception.InvalidFileException;
import vkr.verifier.model.Report;
import vkr.verifier.model.ReportStatus;
import vkr.verifier.repository.ReportRepository;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final static String FILE_CORRECT_MIME_TYPE =
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    private final ReportRepository reportRepository;
    private final FileStorageService fileStorageService;
    private final ReportMapper reportMapper;

    @Transactional
    public ReportGeneralInfoResponseDto createReport(MultipartFile file) {
        validateDocx(file);
        UUID reportId = UUID.randomUUID();
        Report report = Report.builder()
            .id(reportId)
            .reportStatus(ReportStatus.PROCESSING)
            .reportJson(null)
            .build();
        reportRepository.save(report);
        fileStorageService.save(file, reportId);
        return ReportGeneralInfoResponseDto.builder()
            .id(report.getId())
            .reportStatus(report.getReportStatus())
            .createdAt(report.getCreatedAt())
            .build();
    }

    private void validateDocx(MultipartFile file) {
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
