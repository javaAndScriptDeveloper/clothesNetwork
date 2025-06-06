package com.example.company.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "invites")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InviteEntity {

    @Id
    UUID id;

    @Column(nullable = false)
    String url;

    @Column(nullable = false)
    Boolean used;

    @CreationTimestamp
    Instant createdAt;

    @UpdateTimestamp
    Instant updatedAt;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne
    @JoinColumn(name = "brand_id", nullable = false)
    BrandEntity brand;
}
