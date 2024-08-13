package com.example.company.dto.response;

import com.example.company.dto.post.PostPublicationTimeRequest;
import com.example.company.dto.post.PostViewCondition;
import com.example.company.enums.AuthorType;
import java.util.List;
import java.util.UUID;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostResponse {

    UUID id;

    AuthorType authorType;

    Long authorId;

    String textContent;

    Boolean visible;

    PostPublicationTimeRequest publicationTime;

    List<PostViewCondition> viewConditions;
}
