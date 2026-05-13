package com.sastreria.gestiondeprecios.customer;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.sastreria.gestiondeprecios.customer.exceptions.CustomerInvalidActiveException;
import com.sastreria.gestiondeprecios.customer.exceptions.CustomerInvalidNameException;
import com.sastreria.gestiondeprecios.util.StringNormalizer;

import java.time.LocalDateTime;

@Entity
@Table(name = "customers")
@Getter
@EntityListeners(AuditingEntityListener.class)

public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String name;

    @Column(nullable = false)
    private boolean active;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected Customer() {
    }

    public Customer(String name) {
        updateName(name);
        this.active = true;
    }

    public void activate() {
        if (isActive())
            throw new CustomerInvalidActiveException("El usuario ya se encuentra activo.");
        this.active = true;
    }

    public boolean isActive() {
        return this.active;
    }

    public void desactivate() {
        if (!isActive())
            throw new CustomerInvalidActiveException("El usuario ya se encuentra desactivado.");
        this.active = false;
    }

    public void updateName(String name) {
        if (name == null) {
            throw new CustomerInvalidNameException("El nombre es obligatorio.");
        }

        String formattedName = StringNormalizer.normalize(name);

        if (formattedName.isBlank()) {
            throw new CustomerInvalidNameException("El nombre es obligatorio.");
        }

        this.name = formattedName;
    }
}
