# Exercise 02: AnnotationConfig

## Exercise Overview
The objective of **Exercise 02** is to **migrate** the Spring application from XML-based configuration (`context.xml`) to **pure annotation-based configuration** — eliminating all XML while preserving identical functionality.

Additionally, a `UsersService` / `UsersServiceImpl` service layer is introduced, and an **integration test** validates the service using an **in-memory H2 database** with a separate test configuration.

---

## Concepts

1. **`@Configuration` (replaces `context.xml`)**
   - Marks a class as a Spring configuration source. Spring creates a CGLIB proxy subclass to intercept `@Bean` method calls and enforce singleton semantics — calling a `@Bean` method multiple times returns the same instance.
2. **`@Bean` (replaces `<bean>` XML elements)**
   - Declares a method's return value as a Spring-managed bean. The method name becomes the bean name. Used for infrastructure beans (DataSources) where you control instantiation logic.
3. **`@Repository` / `@Service` (stereotype annotations)**
   - Specializations of `@Component` — mark classes for auto-detection by `@ComponentScan`. `@Repository` additionally enables `PersistenceExceptionTranslation` (auto-translates `SQLException` → Spring's `DataAccessException`).
4. **`@ComponentScan` (replaces `<context:component-scan>`)**
   - Tells Spring to scan packages for classes annotated with `@Component` / `@Repository` / `@Service` / `@Controller`. Uses ASM bytecode analysis (not reflection) to read `.class` files without loading them.
   - `basePackageClasses` is the type-safe alternative to `basePackages` — refactor-friendly.
5. **`@PropertySource` & `@Value` (replaces `<context:property-placeholder>`)**
   - `@PropertySource` loads a `.properties` file into Spring's `Environment`. `@Value("${key}")` injects resolved property values into fields via reflection.
6. **`@Autowired` & `@Qualifier`**
   - `@Autowired` tells Spring to auto-inject a dependency by type. When multiple beans of the same type exist, `@Qualifier("beanName")` disambiguates by specifying the exact bean name.
7. **`@SpringJUnitConfig` (integration testing)**
   - Combines `@ExtendWith(SpringExtension.class)` + `@ContextConfiguration` — boots a Spring context for JUnit 5 tests using the specified configuration class.
8. **`EmbeddedDatabaseBuilder` (in-memory test database)**
   - Builds an in-memory H2/HSQLDB database, runs SQL scripts to set up schema, and returns a `DataSource`. `MODE=PostgreSQL` enables PostgreSQL syntax compatibility (e.g., `SERIAL`).
9. **`@Import` (selective bean registration)**
   - Cherry-picks specific classes to register as beans — more precise than `@ComponentScan` for test contexts where you only need a subset of beans.

---

## My Implementation

### Configuration (`src/main/java/.../config/ApplicationConfig.java`)
```java
@Configuration
@PropertySource("classpath:db.properties")
@ComponentScan(basePackageClasses = {UsersRepository.class, UsersService.class})
public class ApplicationConfig {

    @Value("${db.url}")     private String jdbcUrl;
    @Value("${db.user}")    private String userName;
    @Value("${db.password}") private String password;
    @Value("${db.driver.name}") private String driverName;

    @Bean
    public DataSource hikariDataSource() { /* HikariDataSource setup */ }

    @Bean
    public DataSource driverManagerDataSource() { /* DriverManagerDataSource setup */ }
}
```

### Repository Layer (auto-discovered via `@ComponentScan`)
```java
@Repository("usersRepositoryJdbc")
public class UsersRepositoryJdbcImpl implements UsersRepository {
    @Autowired
    public UsersRepositoryJdbcImpl(@Qualifier("driverManagerDataSource") DataSource ds) { ... }
}

@Repository("usersRepositoryJdbcTemplate")
public class UsersRepositoryJdbcTemplateImpl implements UsersRepository {
    @Autowired
    public UsersRepositoryJdbcTemplateImpl(@Qualifier("hikariDataSource") DataSource ds) { ... }
}
```

### Service Layer
```java
@Service
public class UsersServiceImpl implements UsersService {
    @Autowired
    public UsersServiceImpl(@Qualifier("usersRepositoryJdbcTemplate") UsersRepository repo) { ... }

    @Override
    public String signUp(String email) {
        String password = UUID.randomUUID().toString().replace("-", "");
        usersRepository.save(new User(null, email, password));
        return password;
    }
}
```

### Test Configuration (`src/test/java/.../config/TestApplicationConfig.java`)
```java
@Configuration
@Import({UsersRepositoryJdbcTemplateImpl.class, UsersServiceImpl.class})
public class TestApplicationConfig {
    @Bean
    public DataSource hikariDataSource() {
        return new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .setName("testdb;MODE=PostgreSQL")
            .addScript("schema.sql")
            .build();
    }
}
```

### Integration Test (`src/test/java/.../services/UsersServiceImplTest.java`)
```java
@SpringJUnitConfig(TestApplicationConfig.class)
public class UsersServiceImplTest {
    @Autowired
    private UsersService usersService;

    @Test
    public void testSignUpMethod() {
        String pass = usersService.signUp("test@test.com");
        assertNotNull(pass, "Returned password should not be null");
    }
}
```

---

## How It Works

1. `Main` creates an `AnnotationConfigApplicationContext(ApplicationConfig.class)`.
2. Spring processes `@PropertySource` → loads `db.properties` into the `Environment`.
3. Spring resolves `@Value` fields → injects DB credentials.
4. Spring executes `@Bean` methods → creates two `DataSource` beans.
5. `@ComponentScan` triggers `ClassPathBeanDefinitionScanner` → discovers `@Repository` and `@Service` classes.
6. Spring instantiates repositories → `@Autowired` + `@Qualifier` resolves which `DataSource` each gets.
7. Spring instantiates `UsersServiceImpl` → `@Qualifier` resolves which `UsersRepository` it gets.
8. `Main` retrieves both repositories by bean name and calls `findAll()`.

For testing:
1. `@SpringJUnitConfig` boots a separate context using `TestApplicationConfig`.
2. `@Import` registers only `UsersRepositoryJdbcTemplateImpl` and `UsersServiceImpl` — no `driverManagerDataSource` ambiguity.
3. `EmbeddedDatabaseBuilder` creates an in-memory H2 database with PostgreSQL compatibility, runs `schema.sql`.
4. Test calls `signUp()` and asserts the returned password is not null.

---

## How to Compile

```bash
cd Module08/ex02/Service
mvn clean compile
```

---

## How to Run

Ensure PostgreSQL is running with the database specified in `db.properties`, then:
```bash
mvn exec:java
```

Expected output:
```text
[User [id=1, email=oualidobbad@gmail.com, password=password1], ...]
[User [id=1, email=oualidobbad@gmail.com, password=password1], ...]
```

---

## How to Run Tests

```bash
mvn clean test
```

Expected output:
```text
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

---

## Important Things to Understand (for Evaluation / Code Review)

1. **Why do DataSource beans stay as `@Bean` methods while repositories use `@Repository`?**
   - `DataSource` implementations (`HikariDataSource`, `DriverManagerDataSource`) are third-party classes — you can't annotate their source code with `@Component`. `@Bean` methods give you explicit control over instantiation of classes you don't own.
2. **Why use `@Qualifier` and not just `@Autowired`?**
   - There are two `DataSource` beans and two `UsersRepository` beans in the container. `@Autowired` alone resolves by type — with multiple beans of the same type, Spring throws `NoUniqueBeanDefinitionException`. `@Qualifier` disambiguates by bean name.
3. **Why use `@Import` instead of `@ComponentScan` in `TestApplicationConfig`?**
   - `@ComponentScan` would discover both repository implementations, but the test context only defines one `DataSource` (`hikariDataSource`). `UsersRepositoryJdbcImpl` requires `driverManagerDataSource` → context creation would fail. `@Import` selectively registers only the beans needed for testing.
4. **What happens if `@PropertySource` is removed but `@Value` annotations remain?**
   - Spring throws `IllegalArgumentException: Could not resolve placeholder 'db.url'` during bean post-processing, before any `@Bean` method runs.
5. **Why `setName("testdb;MODE=PostgreSQL")` on `EmbeddedDatabaseBuilder`?**
   - H2 doesn't natively support PostgreSQL-specific SQL syntax like `SERIAL`. `MODE=PostgreSQL` tells H2 to accept PostgreSQL dialect, allowing reuse of the production `schema.sql` without maintaining a separate test-specific version.
