package com.example.company.exception.notfound;

import com.example.company.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class BrandNotFoundException extends ApplicationException {

    private static final String message = "Brand with id '%s' not found";

    public BrandNotFoundException(Long id) {
        super(HttpStatus.NOT_FOUND, message.formatted(id));
    }
}
