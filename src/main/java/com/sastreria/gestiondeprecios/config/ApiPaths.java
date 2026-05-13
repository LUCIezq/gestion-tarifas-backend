package com.sastreria.gestiondeprecios.config;

public class ApiPaths {
    private static final String API_BASE = "/v1";

    private ApiPaths() {
    }

    public static final class Customers {
        private Customers() {
        }

        public static final String CUSTOMERS_ROOT = API_BASE + "/customers";
        public static final String CUSTOMERS_BY_ID = "/{id}";
    }
}
