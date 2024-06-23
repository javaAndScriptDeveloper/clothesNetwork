package com.example.company.entity;

import com.example.company.enums.AuthorType;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.List;
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
@Table(name = "posts")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    UserEntity userAuthor;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    BrandEntity brandAuthor;

    @Column(name = "author_type")
    @Enumerated(EnumType.STRING)
    AuthorType authorType;

    @Column(name = "text_content")
    String textContent;

    @CreationTimestamp
    Instant createdAt;

    @UpdateTimestamp
    Instant updatedAt;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToMany(mappedBy = "posts")
    List<FeedEntity> feeds;
}
