package vkr.verifier.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import vkr.verifier.dto.response.ReportGeneralInfoResponseDto;
import vkr.verifier.dto.response.ReportResponseDto;
import vkr.verifier.dto.response.ReportsGeneralInfoPageDto;
import vkr.verifier.exception.ReportNotFoundException;
import vkr.verifier.mapper.ReportGeneralInfoProjectionMapper;
import vkr.verifier.mapper.ReportMapper;
import vkr.verifier.model.Report;
import vkr.verifier.model.ReportStatus;
import vkr.verifier.repository.ReportRepository;
import vkr.verifier.repository.projection.ReportGeneralInfoProjection;
import vkr.verifier.util.FormatFileValidator;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final FileStorageService fileStorageService;

    @Transactional
    public ReportGeneralInfoResponseDto createReport(MultipartFile file) {
        FormatFileValidator.validateDocx(file);
        UUID reportId = UUID.randomUUID();
        Report report = Report.builder()
            .id(reportId)
            .reportStatus(ReportStatus.PROCESSING)
            .reportJson(null)
            .createdAt(Instant.now())
            .build();
        reportRepository.save(report);
        String fileName = reportId.toString() + ".docx";
        fileStorageService.save(file, fileName);
        return ReportMapper.toReportGeneralInfoResponseDto(report);
    }

    @Transactional(readOnly = true)
    public ReportResponseDto getById(UUID id) {
        Report report = reportRepository.findById(id)
            .orElseThrow(
                () -> new ReportNotFoundException("Отчет не найден: " + id)
            );
        return ReportMapper.toReportResponseDto(report);
    }

    @Transactional(readOnly = true)
    public ReportsGeneralInfoPageDto getAll(String partUuid, Pageable pageable) {
        Page<ReportGeneralInfoProjection> page = reportRepository.findAllGeneralInfo(
            partUuid,
            pageable
        );
        return ReportGeneralInfoProjectionMapper.toPageDto(page);
    }
}
