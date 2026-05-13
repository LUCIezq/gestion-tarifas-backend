# AGENTS.md - Spring Boot Project Guidelines

## Project Overview
Spring Boot 3.3.5 application for managing prices (gestiondeprecios). Java 17.

## Build & Test Commands

> Prerequisito: Java 17. En Windows, asegurá `JAVA_HOME` apuntando al JDK (el wrapper `mvnw.cmd` falla si no está configurado).

### Run Application
```bash
./mvnw spring-boot:run
.\mvnw.cmd spring-boot:run   # Windows (PowerShell)
```

### Build
```bash
./mvnw clean package
.\mvnw.cmd clean package     # Windows (PowerShell)
```

### Run Tests
```bash
./mvnw test                    # All tests
./mvnw test -Dtest=ClassName   # Single test class
./mvnw test -Dtest=ClassName#methodName  # Single test method
.\mvnw.cmd test                 # Windows (PowerShell)
```

### Docker
```bash
docker-compose up -d    # Start MySQL + Adminer
docker compose up -d    # Docker Compose v2
```

## Architecture

### Package Structure
```
com.sastreria.gestiondeprecios/
├── config/         # ApiPaths, AuditConfig, GlobalExceptionHandler
├── customer/       # Customers feature (entity, controller, service, repository, dto, mapper, exceptions)
├── enums/          # Enumerations (Gender)
├── gender/         # Read-only genders endpoint
├── products/       # Products feature (entity, controller, service, repository, dto, mapper, exceptions)
└── util/           # Utilities (ErrorResponse)
```

### Layered Architecture
1. **Controller** - REST endpoints, validation, logging
2. **Service (Interface)** - Business logic contract
3. **ServiceImpl** - Business logic implementation (use `@Transactional` when needed, e.g. `customer/CustomerServiceImpl`)
4. **Repository** - Spring Data JPA data access
5. **Mapper** - MapStruct `@Mapper(componentModel = "spring")` converting between DTOs and Entities
6. **DTOs** - Records for request/response

## Code Style Guidelines

### Naming Conventions
- **Classes**: PascalCase (`Product`, `CustomerServiceImpl`)
- **Interfaces**: PascalCase (`ProductService`, `CustomerService`)
- **Methods**: camelCase (`findByName`, `existsByName`)
- **Variables**: camelCase (`product`, `customerRequest`)
- **Packages**: lowercase with dots (`com.sastreria.gestiondeprecios`)
- **Enums**: UPPER_SNAKE_CASE (`HOMBRE`, `MUJER`)
- **DTOs**: PascalCase with suffix `Request` or `Response` (`CustomerRequest`, `ProductResponse`)
- **Mappers**: PascalCase with suffix `Mapper` (`ProductMapper`)

### Java Records for DTOs
```java
// Request DTOs with validation
public record CustomerRequest(
        @NotBlank(message = "El name es obligatorio")
        @Size(min = 3, max = 50, message = "El nombre debe tener entre 3 y 50 caracteres.")
        String name
) { }

// Response DTOs with @Builder
@Builder
public record CustomerResponse(
        Long id,
        boolean active,
        String name,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime createdAt
) { }
```

### Entities
```java
@Entity
@Table(name = "products")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal priceReference;
}
```

### Controllers
```java
@RestController
@RequestMapping(ApiPaths.Customers.CUSTOMERS_ROOT)
@RequiredArgsConstructor
@Slf4j
@Validated
public class CustomerController {
    private final CustomerService customerService;
    private final CustomerMapper customerMapper;

    @PostMapping
    public ResponseEntity<CustomerResponse> create(
            @Valid @RequestBody CustomerRequest customerRequest,
            UriComponentsBuilder uriComponentsBuilder) {
        log.info("Request para crear usuario({})", customerRequest.name());
        Customer saved = customerService.create(customerMapper.customerDtoToCustomer(customerRequest));
        URI uri = uriComponentsBuilder
                .path(ApiPaths.Customers.CUSTOMERS_BY_ID)
                .buildAndExpand(saved.getId())
                .toUri();
        return ResponseEntity.created(uri).body(customerMapper.customerToCustomerDto(saved));
    }
}
```

### Service Implementation
```java
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {
    private final ProductRepository repository;

    @Override
    public Product save(Product product) {
        String name = product.getName();
        if (existsByName(name)) {
            throw new ProductAlreadyExist("Ya se encuentra registrado un producto con este nombre");
        }
        return repository.save(product);
    }
}
```

### Mappers
```java
@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    Product productDtoToProduct(ProductRequest productRequest);

    ProductResponse productToProductDto(Product saved);
}
```

### Custom Exceptions
```java
// Custom exception
public class ProductAlreadyExist extends RuntimeException {
    public ProductAlreadyExist(String message) { super(message); }
}

// Global exception handler
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handle(
            MethodArgumentNotValidException exception, HttpServletRequest request) {
        Map<String, String> errors = exception.getBindingResult().getFieldErrors().stream()
                .collect(HashMap::new, (map, error) -> map.put(error.getField(), error.getDefaultMessage()), HashMap::putAll);

        ErrorResponse response = ErrorResponse.builder()
                .message("Error de validacion")
                .status(HttpStatus.BAD_REQUEST.value())
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .errors(errors)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
```

### Imports Order (IDE will handle, but be aware)
1. `java.*`
2. `jakarta.*`
3. Third-party (`org.*`)
4. Project imports (`com.sastreria.*`)

### Lombok Usage
- `@Getter`, `@Builder`, `@AllArgsConstructor`, `@NoArgsConstructor` for entities (with `@Setter` on mutable fields like `customer/Customer#active`)
- `@RequiredArgsConstructor` for services, controllers, mappers
- `@Slf4j` for logging in controllers and exception handlers

### Annotations Order on Classes
1. `@Entity`, `@Table`, `@RestController`, etc.
2. `@RequestMapping` or similar mappings
3. Lombok annotations (`@Getter`, `@RequiredArgsConstructor`, etc.)
4. `@Slf4j`

### API Versioning
- Use `/v1/` prefix for all endpoints (see `config/ApiPaths.java`): `/v1/customers`, `/v1/products`, `/v1/genders`

### Validation
- Use Jakarta Validation (`@Valid`) on controller method parameters
- Use record DTOs with constraint annotations for request validation
- Spanish error messages for validation constraints

### Testing
- H2 in-memory database for tests (`application-test.yml`)
- Use `@SpringBootTest` or `@WebMvcTest` as appropriate
- `GestiondepreciosApplicationTests` uses `@ActiveProfiles("test")`
