package vkr.verifier.repository.projection;

import java.time.Instant;
import java.util.UUID;

import vkr.verifier.model.ReportStatus;

public interface ReportGeneralInfoProjection {
    UUID getId();
    ReportStatus getReportStatus();
    Instant getCreatedAt();
}
