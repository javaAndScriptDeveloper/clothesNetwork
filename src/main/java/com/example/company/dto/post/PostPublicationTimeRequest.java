package com.example.company.dto.post;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostPublicationTimeRequest {

    Long value; // TODO In future can be changed to LocalDate with timezone
}
