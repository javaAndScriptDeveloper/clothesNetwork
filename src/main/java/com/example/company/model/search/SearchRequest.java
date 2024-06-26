package com.example.company.model.search;

import com.example.company.enums.SearchOperator;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SearchRequest<T> {

    Class<T> clazz;
    List<FilteringField> filteringFields;

    @Data
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class FilteringField {

        String fieldName;
        SearchOperator operator;
        Object fieldValue;
    }
}
