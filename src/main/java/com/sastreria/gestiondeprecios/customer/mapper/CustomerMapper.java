package com.sastreria.gestiondeprecios.customer.mapper;

import com.sastreria.gestiondeprecios.customer.Customer;
import com.sastreria.gestiondeprecios.customer.dto.CustomerDetailResponse;
import com.sastreria.gestiondeprecios.customer.dto.CustomerRequest;
import com.sastreria.gestiondeprecios.customer.dto.CustomerResponse;
import org.mapstruct.Mapper;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    CustomerResponse customerToCustomerDto(Customer customer);

    Customer customerDtoToCustomer(CustomerRequest customerRequest);

    CustomerDetailResponse customerToCustomerDetailDto(Customer customer);

    List<CustomerDetailResponse> allCustomerToCustomerDetailResponse(List<Customer> customers);
}
