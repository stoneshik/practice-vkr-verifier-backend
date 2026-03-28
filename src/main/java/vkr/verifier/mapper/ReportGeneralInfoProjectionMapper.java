package vkr.verifier.mapper;

import org.springframework.data.domain.Page;

import vkr.verifier.dto.response.ReportGeneralInfoResponseDto;
import vkr.verifier.dto.response.ReportsGeneralInfoPageDto;
import vkr.verifier.repository.projection.ReportGeneralInfoProjection;

public class ReportGeneralInfoProjectionMapper {
    private ReportGeneralInfoProjectionMapper() {}

    public static ReportGeneralInfoResponseDto toDto(
        ReportGeneralInfoProjection reportGeneralInfoProjection
    ) {
        return ReportGeneralInfoResponseDto.builder()
            .id(reportGeneralInfoProjection.getId())
            .reportStatus(reportGeneralInfoProjection.getReportStatus())
            .createdAt(reportGeneralInfoProjection.getCreatedAt())
            .build();
    }

    public static ReportsGeneralInfoPageDto toPageDto(
        Page<ReportGeneralInfoProjection> page
    ) {
        return ReportsGeneralInfoPageDto.builder()
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .currentPage(page.getNumber())
            .pageSize(page.getSize())
            .elements(
                page.getContent().stream()
                    .map(reportGeneralInfoProjection -> toDto(reportGeneralInfoProjection))
                    .toList()
            )
            .build();
    }
}
