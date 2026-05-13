package com.sastreria.gestiondeprecios.customer;

import com.sastreria.gestiondeprecios.config.ApiPaths;
import com.sastreria.gestiondeprecios.customer.dto.CustomerDetailResponse;
import com.sastreria.gestiondeprecios.customer.dto.CustomerRequest;
import com.sastreria.gestiondeprecios.customer.dto.CustomerResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(ApiPaths.Customers.CUSTOMERS_ROOT)
@RequiredArgsConstructor
@Slf4j
@Validated
public class CustomerController {

        private final CustomerService service;

        @PostMapping
        public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CustomerRequest customerRequest,
                        UriComponentsBuilder uriComponentsBuilder) {

                log.info("Request para crear usuario({})", customerRequest.name());

                CustomerResponse saved = service.create(customerRequest);

                URI uri = uriComponentsBuilder
                                .path(ApiPaths.Customers.CUSTOMERS_BY_ID)
                                .buildAndExpand(saved.id())
                                .toUri();

                return ResponseEntity
                                .created(uri)
                                .body(saved);
        }

        @GetMapping
        public ResponseEntity<List<CustomerDetailResponse>> customers() {
                log.info("Request para traer a todos los clientes");

                List<CustomerDetailResponse> customers = service.getAll();
                return ResponseEntity.ok(
                                customers);
        }

        @GetMapping(ApiPaths.Customers.CUSTOMERS_BY_ID)
        public ResponseEntity<CustomerDetailResponse> getById(
                        @Positive(message = "El id debe ser un numero valido.") @PathVariable Long id) {
                log.info("Request para traer al cliente con id({})", id);
                return ResponseEntity.ok(
                                service.findById(id));
        }

        @DeleteMapping(ApiPaths.Customers.CUSTOMERS_BY_ID)
        public ResponseEntity<Void> desactivate(
                        @Positive(message = "El id debe ser un numero valido.") @PathVariable Long id) {
                log.info("Request para desactivar al cliente con id({})", id);
                service.desactivate(id);
                return ResponseEntity
                                .noContent()
                                .build();
        }
}
