package com.example.company.exception.notfound;

import com.example.company.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class FeedNotFoundException extends ApplicationException {

    private static final String message = "Feed for user with id '%s' not found";

    public FeedNotFoundException(Long userId) {
        super(HttpStatus.NOT_FOUND, message.formatted(userId));
    }
}
