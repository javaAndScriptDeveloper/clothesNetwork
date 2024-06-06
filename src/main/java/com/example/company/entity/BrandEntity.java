package com.example.company.entity;

import jakarta.persistence.*;
import java.util.List;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "brands")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BrandEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "name", nullable = false)
    String name;

    @Column(name = "enabled", nullable = false)
    Boolean enabled;

    @OneToMany(mappedBy = "managedBrand")
    List<UserEntity> managingUsers;

    @ManyToMany(mappedBy = "subscribedBrands")
    List<UserEntity> followingUsers;

    @ManyToMany(mappedBy = "affiliatedBrands")
    List<UserEntity> affiliatedUsers;
}
