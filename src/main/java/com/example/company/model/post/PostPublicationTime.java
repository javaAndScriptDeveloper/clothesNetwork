package com.example.company.model.post;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostPublicationTime {

    Long value; // TODO In future can be changed to LocalDate with timezone
}
