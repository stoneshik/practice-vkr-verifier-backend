package vkr.verifier.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import vkr.verifier.model.Report;
import vkr.verifier.repository.projection.ReportGeneralInfoProjection;

public interface ReportRepository extends JpaRepository<Report, UUID> {
    @Query(
        value = """
            select
                r.id as id,
                r.report_status as reportStatus,
                r.created_at as createdAt
            from reports r
            where (:partUuid is null or :partUuid = '' or cast(r.id as text) ilike concat('%', :partUuid, '%'))
            order by r.created_at desc
            """,
        countQuery = """
            select count(*)
            from reports r
            where (:partUuid is null or :partUuid = '' or cast(r.id as text) ilike concat('%', :partUuid, '%'))
            """,
        nativeQuery = true
    )
    Page<ReportGeneralInfoProjection> findAllGeneralInfo(
        @Param("partUuid") String partUuid,
        Pageable pageable
    );

    @Query(
        value = """
            select *
            from reports
            where report_status = 'PENDING'
            order by created_at
            limit :limit
            for update skip locked
            """,
        nativeQuery = true
    )
    List<Report> lockNextPendingBatch(@Param("limit") int limit);
}
