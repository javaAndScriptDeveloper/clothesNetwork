package com.example.company.entity;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;
import lombok.experimental.FieldDefaults;

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

    @ManyToOne
    @JoinColumn(name = "brand_id", nullable = false)
    BrandEntity brand;
}
