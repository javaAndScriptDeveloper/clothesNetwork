package com.example.company.dto.request;

import com.example.company.dto.post.PostPublicationTimeRequest;
import com.example.company.dto.post.PostViewCondition;
import com.example.company.enums.AuthorType;
import java.util.List;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdatePostRequest {

    AuthorType authorType;

    Long authorId;

    String textContent;

    Boolean visible;

    PostPublicationTimeRequest publicationTime;

    List<PostViewCondition> viewConditions;
}
