package com.example.company.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Data
@Entity
@Table(name = "images")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ImageEntity {

    @Id
    Long id;

    @Lob
    @Column(name = "data", nullable = false)
    byte[] data;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    UserEntity user;
}
