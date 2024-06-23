package com.example.company.exception;

import java.util.Optional;
import lombok.*;
import org.springframework.http.HttpStatus;

@Builder
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ApplicationException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final Optional<String> errorMessage;
    private final Optional<Exception> internalException;

    ApplicationException() {
        this.httpStatus = HttpStatus.BAD_REQUEST;
        this.errorMessage = Optional.empty();
        this.internalException = Optional.empty();
    }

    ApplicationException(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
        this.errorMessage = Optional.empty();
        this.internalException = Optional.empty();
    }

    public ApplicationException(HttpStatus httpStatus, String errorMessage) {
        this.httpStatus = httpStatus;
        this.errorMessage = Optional.of(errorMessage);
        this.internalException = Optional.empty();
    }

    ApplicationException(HttpStatus httpStatus, String errorMessage, Exception runtimeException) {
        this.httpStatus = httpStatus;
        this.errorMessage = Optional.of(errorMessage);
        this.internalException = Optional.of(runtimeException);
    }
}
