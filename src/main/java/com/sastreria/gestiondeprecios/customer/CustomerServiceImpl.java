package com.sastreria.gestiondeprecios.customer;

import com.sastreria.gestiondeprecios.customer.dto.CustomerDetailResponse;
import com.sastreria.gestiondeprecios.customer.dto.CustomerRequest;
import com.sastreria.gestiondeprecios.customer.dto.CustomerResponse;
import com.sastreria.gestiondeprecios.customer.exceptions.CustomerAlreadyExistsException;
import com.sastreria.gestiondeprecios.customer.exceptions.CustomerNotFoundException;
import com.sastreria.gestiondeprecios.customer.mapper.CustomerMapper;
import com.sastreria.gestiondeprecios.util.StringNormalizer;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    /*
     * Deberiamos poder tener dos clientes que se llamen igual -> Buscar distinsion
     * por otro campo.
     */
    @Transactional
    @Override
    public CustomerResponse create(CustomerRequest request) {

        if (existsByName(StringNormalizer.normalize(request.name()))) {
            throw new CustomerAlreadyExistsException("Ya se encuentra un cliente registrado con ese nombre");
        }

        Customer entity = customerMapper.customerDtoToCustomer(request);

        customerRepository.save(entity);
        return customerMapper.customerToCustomerDto(entity);
    }

    @Override
    public boolean existsByName(String name) {
        return customerRepository.existsUserByName((name));
    }

    @Override
    public CustomerDetailResponse findById(Long id) {
        Customer searched = customerRepository.findById(id).orElseThrow(
                () -> new CustomerNotFoundException("Cliente no encontrado"));
        return customerMapper.customerToCustomerDetailDto(searched);
    }

    @Override
    public List<CustomerDetailResponse> getAll() {
        List<Customer> customers = customerRepository.findAll();
        return customerMapper.allCustomerToCustomerDetailResponse(customers);
    }

    @Override
    public void desactivate(Long id) {
        Customer customer = customerRepository
                .findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Usuario no encontrado"));
        customer.desactivate();
        customerRepository.save(customer);
        log.info("Cliente desactivado ({})", id);
    }
}
