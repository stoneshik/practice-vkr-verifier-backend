package vkr.verifier.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportProcessingScheduler {
    private final ReportProcessingService reportProcessingService;

    @Scheduled(fixedDelayString = "${verifier.scheduler.fixed-delay-ms:5000}")
    public void processPendingReports() {
        reportProcessingService.processPendingBatch();
    }
}
