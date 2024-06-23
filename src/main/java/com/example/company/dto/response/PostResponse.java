package com.example.company.dto.response;

import com.example.company.enums.AuthorType;
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
}
