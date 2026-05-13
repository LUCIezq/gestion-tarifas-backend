package com.sastreria.gestiondeprecios.customer.exceptions;

import org.springframework.http.HttpStatus;

import com.sastreria.gestiondeprecios.util.AbstractException;

public class CustomerInvalidActiveException extends AbstractException {

    public CustomerInvalidActiveException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

}
