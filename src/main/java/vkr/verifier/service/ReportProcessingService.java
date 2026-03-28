package vkr.verifier.service;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import vkr.verifier.config.properties.VerifierProperties;
import vkr.verifier.model.Report;
import vkr.verifier.model.ReportStatus;
import vkr.verifier.repository.ReportRepository;

@Service
@RequiredArgsConstructor
public class ReportProcessingService {
    private final VerifierProperties verifierProperties;
    private final ReportRepository reportRepository;
    private final FileStorageService fileStorageService;
    private final VerifierClient verifierClient;

    public void processPendingBatch() {
        List<UUID> reportIds = lockPendingIds(verifierProperties.getBatchSize());
        for (UUID reportId: reportIds) {
            processSingleReport(reportId);
        }
    }

    @Transactional
    public List<UUID> lockPendingIds(int batchSize) {
        List<Report> reports = reportRepository.lockNextPendingBatch(batchSize);
        List<UUID> ids = new ArrayList<>();
        for (Report report: reports) {
            report.setReportStatus(ReportStatus.PROCESSING);
            ids.add(report.getId());
        }
        return ids;
    }

    public void processSingleReport(UUID reportId) {
        String fileName = reportId.toString() + ".docx";
        Path filePath = fileStorageService.getFilePath(fileName);
        try {
            String reportJson = verifierClient.run(filePath);
            completeSuccessfully(reportId, reportJson);
            fileStorageService.delete(fileName);
        } catch (Exception e) {
            markFailed(reportId);
            safeDelete(fileName);
        }
    }

    @Transactional
    public void completeSuccessfully(UUID reportId, String reportJson) {
        Report report = reportRepository.findById(reportId)
            .orElseThrow(
                () -> new IllegalStateException("Report not found: " + reportId)
            );
        Report updatedReport = report.toBuilder()
            .reportStatus(ReportStatus.DONE)
            .reportJson(reportJson)
            .build();
        reportRepository.save(updatedReport);
    }

    @Transactional
    public void markFailed(UUID reportId) {
        Report report = reportRepository.findById(reportId)
            .orElseThrow(
                () -> new IllegalStateException("Report not found: " + reportId)
            );
        Report updatedReport = report.toBuilder()
            .reportStatus(ReportStatus.ERROR)
            .build();
        reportRepository.save(updatedReport);
    }

    private void safeDelete(String fileName) {
        try {
            fileStorageService.delete(fileName);
        } catch (Exception e) {}
    }
}
