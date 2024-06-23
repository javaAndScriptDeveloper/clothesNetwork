package com.example.company.exception;

import java.util.UUID;
import org.springframework.http.HttpStatus;

public class InviteNotFoundException extends ApplicationException {

    private static final String message = "Invite with id '%s' not found";

    public InviteNotFoundException(UUID id) {
        super(HttpStatus.NOT_FOUND, message.formatted(id));
    }
}
