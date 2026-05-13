package com.sastreria.gestiondeprecios.customer.exceptions;

import org.springframework.http.HttpStatus;

import com.sastreria.gestiondeprecios.util.AbstractException;

public class CustomerNotFoundException extends AbstractException {
    public CustomerNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
