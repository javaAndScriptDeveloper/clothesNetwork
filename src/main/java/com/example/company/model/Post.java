package com.example.company.model;

import com.example.company.enums.AuthorType;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Post {

    UUID id;

    AuthorType authorType;

    Long authorId;

    String textContent;
}
