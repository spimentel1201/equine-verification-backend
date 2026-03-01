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
}
