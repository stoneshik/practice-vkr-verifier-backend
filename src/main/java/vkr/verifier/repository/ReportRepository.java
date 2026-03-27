package vkr.verifier.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import vkr.verifier.model.Report;

public interface ReportRepository extends
    JpaRepository<Report, UUID>,
    JpaSpecificationExecutor<Report> {
}
