package vkr.verifier.mapper;

import vkr.verifier.dto.response.ReportResponseDto;
import vkr.verifier.model.Report;

public class ReportMapper {
    private ReportMapper() {}

    public static ReportResponseDto toReportResponseDto(Report report) {
        return ReportResponseDto.builder()
            .id(report.getId())
            .reportStatus(report.getReportStatus())
            .reportJson(report.getReportJson())
            .createdAt(report.getCreatedAt())
            .build();
    }
}
