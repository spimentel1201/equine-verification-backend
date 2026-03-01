package com.horsetrust.repositories.specifications;

import com.horsetrust.models.entities.Horse;
import com.horsetrust.models.entities.Listing;
import com.horsetrust.models.enums.ListingStatus;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class ListingSpecification {

    public static Specification<Listing> isStatus(ListingStatus status) {
        return (root, query, builder) -> {
            if (Long.class != query.getResultType()) { // No fetch paged count query
                root.fetch("horse", JoinType.INNER);
                root.fetch("seller", JoinType.INNER);
            }
            return status == null ? builder.conjunction() : builder.equal(root.get("status"), status);
        };
    }

    public static Specification<Listing> titleOrDescriptionContains(String keyword) {
        return (root, query, builder) -> {
            if (keyword == null || keyword.isBlank())
                return builder.conjunction();
            String likePattern = "%" + keyword.toLowerCase() + "%";
            return builder.or(
                    builder.like(builder.lower(root.get("title")), likePattern),
                    builder.like(builder.lower(root.get("description")), likePattern));
        };
    }

    public static Specification<Listing> priceBetween(BigDecimal minPrice, BigDecimal maxPrice) {
        return (root, query, builder) -> {
            if (minPrice != null && maxPrice != null) {
                return builder.between(root.get("price"), minPrice, maxPrice);
            } else if (minPrice != null) {
                return builder.greaterThanOrEqualTo(root.get("price"), minPrice);
            } else if (maxPrice != null) {
                return builder.lessThanOrEqualTo(root.get("price"), maxPrice);
            }
            return builder.conjunction();
        };
    }

    public static Specification<Listing> locationContains(String location) {
        return (root, query, builder) -> {
            if (location == null || location.isBlank())
                return builder.conjunction();
            return builder.like(builder.lower(root.get("location")), "%" + location.toLowerCase() + "%");
        };
    }

    public static Specification<Listing> horseBreedContains(String breed) {
        return (root, query, builder) -> {
            if (breed == null || breed.isBlank())
                return builder.conjunction();
            Join<Listing, Horse> horseJoin = root.join("horse", JoinType.INNER);
            return builder.like(builder.lower(horseJoin.get("breed")), "%" + breed.toLowerCase() + "%");
        };
    }
}
