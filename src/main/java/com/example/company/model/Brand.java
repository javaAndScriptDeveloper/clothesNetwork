package com.example.company.model;

import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Brand {

    Long id;

    Long authorId;

    String name;

    Boolean enabled;

    List<Image> profileImages;
}
