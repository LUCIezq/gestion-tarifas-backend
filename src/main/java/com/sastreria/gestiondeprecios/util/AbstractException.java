package com.sastreria.gestiondeprecios.util;

import org.springframework.http.HttpStatus;

public class AbstractException extends RuntimeException {
    private final HttpStatus httpStatus;

    public AbstractException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getStatus() {
        return httpStatus;
    }
}
