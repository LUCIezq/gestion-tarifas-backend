package com.sastreria.gestiondeprecios.customer.dto;

import lombok.Builder;

@Builder
public record CustomerDetailResponse(
        Long id,
        String name,
        Boolean active) {
}
