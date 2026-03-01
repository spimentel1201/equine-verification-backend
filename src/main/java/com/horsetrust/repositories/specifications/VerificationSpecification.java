package com.horsetrust.repositories.specifications;

import com.horsetrust.models.entities.Verification;
import com.horsetrust.models.enums.VerificationStatus;
import com.horsetrust.models.enums.VerificationTarget;
import org.springframework.data.jpa.domain.Specification;

public class VerificationSpecification {

    public static Specification<Verification> hasStatus(VerificationStatus status) {
        return (root, query, builder) -> {
            if (status == null)
                return builder.conjunction();
            return builder.equal(root.get("status"), status);
        };
    }

    public static Specification<Verification> hasTarget(VerificationTarget target) {
        return (root, query, builder) -> {
            if (target == null)
                return builder.conjunction();
            return builder.equal(root.get("target"), target);
        };
    }

    public static Specification<Verification> createdAtBetween(java.time.LocalDateTime startDate,
            java.time.LocalDateTime endDate) {
        return (root, query, builder) -> {
            if (startDate != null && endDate != null) {
                return builder.between(root.get("createdAt"), startDate, endDate);
            } else if (startDate != null) {
                return builder.greaterThanOrEqualTo(root.get("createdAt"), startDate);
            } else if (endDate != null) {
                return builder.lessThanOrEqualTo(root.get("createdAt"), endDate);
            }
            return builder.conjunction();
        };
    }
}
