package com.horsetrust.repositories.specifications;

import com.horsetrust.models.entities.Evidence;
import com.horsetrust.models.enums.EvidenceStatus;
import com.horsetrust.models.enums.EvidenceType;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.JoinType;
import java.util.UUID;

public class EvidenceSpecification {

    public static Specification<Evidence> isType(EvidenceType type) {
        return (root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch("uploader", JoinType.INNER);
                root.fetch("listing", JoinType.LEFT);
                root.fetch("horse", JoinType.LEFT);
            }
            if (type == null)
                return builder.conjunction();
            return builder.equal(root.get("type"), type);
        };
    }

    public static Specification<Evidence> isStatus(EvidenceStatus status) {
        return (root, query, builder) -> {
            if (status == null)
                return builder.conjunction();
            return builder.equal(root.get("status"), status);
        };
    }

    public static Specification<Evidence> hasListing(UUID listingId) {
        return (root, query, builder) -> {
            if (listingId == null)
                return builder.conjunction();
            return builder.equal(root.get("listing").get("id"), listingId);
        };
    }

    public static Specification<Evidence> hasHorse(UUID horseId) {
        return (root, query, builder) -> {
            if (horseId == null)
                return builder.conjunction();
            return builder.equal(root.get("horse").get("id"), horseId);
        };
    }

    public static Specification<Evidence> hasUploader(UUID uploaderId) {
        return (root, query, builder) -> {
            if (uploaderId == null)
                return builder.conjunction();
            return builder.equal(root.get("uploader").get("id"), uploaderId);
        };
    }
}
