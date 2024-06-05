package com.example.company.exception;

import org.springframework.http.HttpStatus;

public class NoAuthenticatedUserException extends ApplicationException {

    public NoAuthenticatedUserException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
