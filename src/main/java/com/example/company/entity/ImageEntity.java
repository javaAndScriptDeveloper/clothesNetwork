package com.example.company.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Entity
@Table(name = "images")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ImageEntity {

    @Id
    private Long id;

    @Lob
    @Column(name = "data", nullable = false)
    private byte[] data;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
}
