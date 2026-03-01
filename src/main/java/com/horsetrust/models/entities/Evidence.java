package com.horsetrust.models.entities;

import com.horsetrust.models.enums.EvidenceStatus;
import com.horsetrust.models.enums.EvidenceType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "evidences")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Evidence {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @NotNull(message = "Evidence type is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EvidenceType type;

    @NotNull(message = "Evidence status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private EvidenceStatus status = EvidenceStatus.PENDING_REVIEW;

    @NotBlank(message = "File URL is required")
    @Size(max = 500, message = "File URL must not exceed 500 characters")
    @Column(nullable = false, length = 500)
    private String fileUrl;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    @Column(length = 500)
    private String description;

    @Size(max = 2000, message = "Metadata must not exceed 2000 characters")
    @Column(length = 2000)
    private String metadata;

    @Column(length = 255)
    private String cloudinaryPublicId;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime uploadedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "listing_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Listing listing;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "horse_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Horse horse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploader_id", nullable = false)
    @NotNull(message = "Uploader is required")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User uploader;
}
