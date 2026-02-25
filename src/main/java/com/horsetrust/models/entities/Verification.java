package com.horsetrust.models.entities;

import com.horsetrust.models.enums.VerificationStatus;
import com.horsetrust.models.enums.VerificationTarget;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "verifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Verification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @NotNull(message = "Verification target is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private VerificationTarget target;

    @NotNull(message = "Target ID is required")
    @Column(nullable = false)
    private UUID targetId;

    @NotNull(message = "Verification status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private VerificationStatus status = VerificationStatus.PENDING;

    @Size(max = 2000, message = "Notes must not exceed 2000 characters")
    @Column(length = 2000)
    private String notes;

    private LocalDateTime validUntil;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verifier_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User verifier;
}
