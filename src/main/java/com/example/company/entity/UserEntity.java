package com.example.company.entity;

import com.example.company.enums.Permission;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "username", nullable = false)
    String username;

    @Column(name = "phone_number")
    String phoneNumber;

    @Column(name = "password")
    String password;

    @Column(name = "email")
    String email;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    List<ImageEntity> profileImages;

    @ManyToOne
    @JoinTable(
            name = "users_brands_management",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "brand_id"))
    BrandEntity managedBrand;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_brands_affiliation",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "brand_id"))
    List<BrandEntity> affiliatedBrands;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_brands_subscriptions",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "brand_id"))
    List<BrandEntity> subscribedBrands;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "users_permissions", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "permission")
    private Set<Permission> permissions = new HashSet<>();
}
