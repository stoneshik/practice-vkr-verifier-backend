package vkr.verifier.specification;

import org.springframework.data.jpa.domain.Specification;

import vkr.verifier.model.Report;

public class ReportSpecifications {
    private ReportSpecifications() {}

    public static Specification<Report> uuidContains(String partUuid) {
        return (root, query, cb) -> {
            if (partUuid == null || partUuid.isBlank()) {
                return cb.conjunction();
            }
            return cb.like(
                cb.lower(root.get("id").as(String.class)),
                "%" + partUuid.trim().toLowerCase() + "%"
            );
        };
    }
}
