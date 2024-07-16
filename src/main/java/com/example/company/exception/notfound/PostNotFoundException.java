package com.example.company.exception.notfound;

import com.example.company.exception.ApplicationException;
import java.util.UUID;
import org.springframework.http.HttpStatus;

public class PostNotFoundException extends ApplicationException {

    private static final String message = "Post with id '%s' not found";

    public PostNotFoundException(UUID id) {
        super(HttpStatus.NOT_FOUND, message.formatted(id));
    }
}
