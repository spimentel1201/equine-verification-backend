package com.horsetrust.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "horse_photos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HorsePhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @NotBlank
    @Column(nullable = false, length = 500)
    private String imageUrl;

    @Column(length = 255)
    private String cloudinaryPublicId;

    @Column(nullable = false)
    private Integer displayOrder;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime uploadedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "horse_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Horse horse;
}
