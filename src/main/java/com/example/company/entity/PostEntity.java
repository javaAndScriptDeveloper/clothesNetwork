package com.example.company.entity;

import com.example.company.entity.info.ViewConditionInfo;
import com.example.company.enums.AuthorType;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "posts")
@Builder(toBuilder = true)
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
    @ManyToMany(mappedBy = "posts", cascade = CascadeType.PERSIST)
    List<FeedEntity> feeds;

    @Column(name = "publication_time")
    Instant publicationTime;

    @Column(name = "is_posted")
    Boolean posted;

    @Column(name = "is_visible")
    Boolean visible;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "view_conditions")
    List<ViewConditionInfo> viewConditions;
}
