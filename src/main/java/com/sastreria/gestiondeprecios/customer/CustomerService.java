package com.sastreria.gestiondeprecios.customer;

import java.util.List;

import com.sastreria.gestiondeprecios.customer.dto.CustomerDetailResponse;
import com.sastreria.gestiondeprecios.customer.dto.CustomerRequest;
import com.sastreria.gestiondeprecios.customer.dto.CustomerResponse;

public interface CustomerService {
    CustomerResponse create(CustomerRequest request);

    boolean existsByName(String name);

    CustomerDetailResponse findById(Long id);

    List<CustomerDetailResponse> getAll();

    void desactivate(Long id);
}
