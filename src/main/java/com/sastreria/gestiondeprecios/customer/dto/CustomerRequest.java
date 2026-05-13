package com.sastreria.gestiondeprecios.customer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CustomerRequest(
                @NotBlank(message = "El name es obligatorio") @Size(min = 3, max = 50, message = "El nombre debe tener entre 3 y 50 caracteres.") String name) {
}
