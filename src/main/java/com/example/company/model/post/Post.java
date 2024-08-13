package com.example.company.model.post;

import com.example.company.enums.AuthorType;
import com.example.company.enums.SearchOperator;
import java.util.List;
import java.util.UUID;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Post {

    UUID id;
    AuthorType authorType;
    Long authorId;
    String textContent;
    Boolean visible;
    PostPublicationTime publicationTime;
    List<ViewCondition> viewConditions;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class ViewCondition {

        String fieldName;
        SearchOperator operator;
        Object fieldValue;
    }
}
