package com.horsetrust.repositories.specifications;

import com.horsetrust.models.entities.Horse;
import com.horsetrust.models.enums.HorseGender;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public class HorseSpecification {

    public static Specification<Horse> nameContains(String name) {
        return (root, query, builder) -> {
            if (Long.class != query.getResultType()) { // Prevent N+1
                root.fetch("owner", JoinType.INNER);
            }
            if (name == null || name.isBlank())
                return builder.conjunction();
            return builder.like(builder.lower(root.get("name")), "%" + name.toLowerCase() + "%");
        };
    }

    public static Specification<Horse> breedContains(String breed) {
        return (root, query, builder) -> {
            if (breed == null || breed.isBlank())
                return builder.conjunction();
            return builder.like(builder.lower(root.get("breed")), "%" + breed.toLowerCase() + "%");
        };
    }

    public static Specification<Horse> ageBetween(Integer minAge, Integer maxAge) {
        return (root, query, builder) -> {
            if (minAge != null && maxAge != null) {
                return builder.between(root.get("age"), minAge, maxAge);
            } else if (minAge != null) {
                return builder.greaterThanOrEqualTo(root.get("age"), minAge);
            } else if (maxAge != null) {
                return builder.lessThanOrEqualTo(root.get("age"), maxAge);
            }
            return builder.conjunction();
        };
    }

    public static Specification<Horse> isGender(HorseGender gender) {
        return (root, query, builder) -> {
            if (gender == null)
                return builder.conjunction();
            return builder.equal(root.get("gender"), gender);
        };
    }
}
