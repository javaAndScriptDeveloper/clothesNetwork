package com.example.company.exception.notfound;

import com.example.company.exception.ApplicationException;
import java.util.UUID;
import org.springframework.http.HttpStatus;

public class InviteNotFoundException extends ApplicationException {

    private static final String message = "Invite with id '%s' not found";

    public InviteNotFoundException(UUID id) {
        super(HttpStatus.NOT_FOUND, message.formatted(id));
    }
}
