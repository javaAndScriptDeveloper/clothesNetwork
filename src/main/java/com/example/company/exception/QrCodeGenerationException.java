package com.example.company.exception;

import org.springframework.http.HttpStatus;

public class QrCodeGenerationException extends ApplicationException {

    private static final String message = "Failed to generate qr code for content: '%s'";

    public QrCodeGenerationException(String content, Exception runtimeException) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message.formatted(content), runtimeException);
    }
}
