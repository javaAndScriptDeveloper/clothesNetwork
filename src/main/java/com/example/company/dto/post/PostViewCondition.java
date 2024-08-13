package com.example.company.dto.post;

import com.example.company.enums.SearchOperator;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostViewCondition {

    String fieldName;
    SearchOperator operator;
    Object fieldValue;
}
