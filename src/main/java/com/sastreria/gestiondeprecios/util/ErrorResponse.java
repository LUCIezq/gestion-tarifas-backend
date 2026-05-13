package com.sastreria.gestiondeprecios.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Map;

@Builder
public record ErrorResponse(
                String message,
                int status,
                LocalDateTime timestamp,
                String method,
                String path,
                @JsonInclude(JsonInclude.Include.NON_NULL) Map<String, String> errors) {
}
