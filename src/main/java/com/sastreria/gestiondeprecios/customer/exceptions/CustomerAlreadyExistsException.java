package com.sastreria.gestiondeprecios.customer.exceptions;

import org.springframework.http.HttpStatus;
import com.sastreria.gestiondeprecios.util.AbstractException;

public class CustomerAlreadyExistsException extends AbstractException {

    public CustomerAlreadyExistsException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
