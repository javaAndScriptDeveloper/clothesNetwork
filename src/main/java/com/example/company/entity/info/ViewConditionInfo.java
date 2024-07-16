package com.example.company.entity.info;

import com.example.company.enums.SearchOperator;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ViewConditionInfo {

    String fieldName;
    SearchOperator operator;
    Object fieldValue;
}
