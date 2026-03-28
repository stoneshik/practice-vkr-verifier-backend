package vkr.verifier.controller;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import vkr.verifier.dto.response.ReportGeneralInfoResponseDto;
import vkr.verifier.dto.response.ReportResponseDto;
import vkr.verifier.dto.response.ReportsGeneralInfoPageDto;
import vkr.verifier.service.ReportService;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ReportGeneralInfoResponseDto> upload(
        @RequestParam("file") MultipartFile file
    ) {
        return ResponseEntity
            .accepted()
            .body(reportService.createReport(file));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReportResponseDto> getById(@PathVariable UUID id) {
        return ResponseEntity
            .ok()
            .body(reportService.getById(id));
    }

    @GetMapping
    public ResponseEntity<ReportsGeneralInfoPageDto> getAll(
        @RequestParam(required = false) String partUuid,
        @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity
            .ok()
            .body(reportService.getAll(partUuid, pageable));
    }
}
