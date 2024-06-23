package com.example.company.dto.request;

import com.example.company.enums.AuthorType;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreatePostRequest {

    AuthorType authorType;

    Long authorId;

    String textContent;
}
