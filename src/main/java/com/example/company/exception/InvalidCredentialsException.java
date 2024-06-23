package com.example.company.exception;

import org.springframework.http.HttpStatus;

public class InvalidCredentialsException extends ApplicationException {

    public InvalidCredentialsException() {
        super(HttpStatus.BAD_REQUEST, "Credentials are in invalid format");
    }
}
