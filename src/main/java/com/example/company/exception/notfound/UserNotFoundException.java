package com.example.company.exception.notfound;

import com.example.company.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class UserNotFoundException extends ApplicationException {

    private static final String message = "User with id '%s' not found";

    public UserNotFoundException(Long id) {
        super(HttpStatus.NOT_FOUND, message.formatted(id));
    }
}
