package com.example.company.exception;

import org.springframework.http.HttpStatus;

public class PdfGenerationException extends ApplicationException {

    private static final String message = "Failed to generate pdf file";

    public PdfGenerationException(Exception runtimeException) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message, runtimeException);
    }
}
