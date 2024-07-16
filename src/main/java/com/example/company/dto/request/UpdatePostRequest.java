package com.example.company.dto.request;

import com.example.company.enums.AuthorType;
import com.example.company.enums.SearchOperator;
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

    List<ViewConditionRequest> viewConditions;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class ViewConditionRequest {

        String fieldName;
        SearchOperator operator;
        Object fieldValue;
    }
}
