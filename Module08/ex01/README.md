# Exercise 01: JdbcTemplate

## Exercise Overview
The objective of **Exercise 01** is to implement a **data access layer** using Spring's `JdbcTemplate` and standard JDBC, with all beans and database connectivity configured via **XML** (`context.xml`).

The application must:
1. Define a `User` model with `id`, `email`, and `password` fields.
2. Implement `CrudRepository<T>` interface with `findById`, `findAll`, `save`, `update`, `delete` methods.
3. Extend it as `UsersRepository` with an additional `findByEmail` method.
4. Provide two implementations: `UsersRepositoryJdbcImpl` (raw JDBC with `PreparedStatement`) and `UsersRepositoryJdbcTemplateImpl` (using `NamedParameterJdbcTemplate`).
5. Configure two `DataSource` beans (`DriverManagerDataSource` and `HikariDataSource`) in `context.xml` with externalized properties from `db.properties`.

---

## Concepts

1. **DataSource Abstraction (`javax.sql.DataSource`)**
   - A standard Java interface for obtaining database connections. Spring provides implementations like `DriverManagerDataSource` (simple, no pooling) and integrates with `HikariDataSource` (production-grade connection pooling).
2. **NamedParameterJdbcTemplate**
   - Spring's higher-level JDBC abstraction that eliminates boilerplate (connection management, exception handling, ResultSet cleanup). Uses named parameters (`:email`) instead of positional `?` placeholders for readability.
3. **RowMapper**
   - A functional interface that maps each row of a `ResultSet` to a Java object. Eliminates repetitive `rs.getString(...)` boilerplate across multiple query methods.
4. **Property Placeholders (`<context:property-placeholder>`)**
   - Externalizes database credentials into `db.properties` and resolves `${db.url}`, `${db.user}`, etc. at container startup — keeping secrets out of XML.
5. **HikariCP Connection Pooling**
   - Maintains a pool of pre-established database connections. Instead of opening/closing a connection per query (expensive), connections are borrowed from and returned to the pool.

---

## My Implementation

### Model (`src/main/java/.../models/User.java`)
- Fields: `Long id`, `String email`, `String password`.
- Overridden `toString()` for readable output.

### Repository Interface Hierarchy
- `CrudRepository<T>`: Generic CRUD operations (`findById`, `findAll`, `save`, `update`, `delete`).
- `UsersRepository extends CrudRepository<User>`: Adds `Optional<User> findByEmail(String email)`.

### UsersRepositoryJdbcImpl
- Uses raw JDBC: `Connection`, `PreparedStatement`, `ResultSet`.
- Constructor accepts `DataSource`, obtains connections via `dataSource.getConnection()`.
- Manual resource management with try-with-resources.

### UsersRepositoryJdbcTemplateImpl
- Uses `NamedParameterJdbcTemplate` constructed from `DataSource`.
- `RowMapper<User>` lambda maps each row to a `User` object.
- Named parameters (`:email`, `:id`) for clarity.
- `GeneratedKeyHolder` captures auto-generated IDs on insert.

### XML Configuration (`src/main/resources/context.xml`)
```xml
<context:property-placeholder location="classpath:db.properties"/>

<bean id="driverManagerDataSource" class="...DriverManagerDataSource">
    <property name="url" value="${db.url}"/>
    <property name="username" value="${db.user}"/>
    <property name="password" value="${db.password}"/>
    <property name="driverClassName" value="${db.driver.name}"/>
</bean>

<bean id="hikariDataSource" class="com.zaxxer.hikari.HikariDataSource">
    <property name="jdbcUrl" value="${db.url}"/>
    ...
</bean>

<bean id="usersRepositoryJdbc" class="...UsersRepositoryJdbcImpl">
    <constructor-arg ref="driverManagerDataSource"/>
</bean>

<bean id="usersRepositoryJdbcTemplate" class="...UsersRepositoryJdbcTemplateImpl">
    <constructor-arg ref="hikariDataSource"/>
</bean>
```

---

## How It Works

1. `Main` creates a `ClassPathXmlApplicationContext` which parses `context.xml`.
2. Spring resolves `${db.url}` etc. from `db.properties`, creates two `DataSource` beans, and injects them into the two repository beans via constructor injection.
3. `Main` retrieves `usersRepositoryJdbc` and `usersRepositoryJdbcTemplate` by bean ID.
4. Demonstrates full CRUD cycle on each: `save` → `findByEmail` → `update` → `findById` → `delete` → `findAll`.

---

## How to Compile

```bash
cd Module08/ex01/Service
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
=================== JDBC Implementation ==================
user added success: User [id=..., email=JDBC@Template, password=null]
user updated succefully: User [id=..., email=JDBC@TemplateUpdated, ...]
...
=================== JDBC Template Implementation ==================
user added success: User [id=..., email=JDBC@Template, password=null]
...
```

---

## Important Things to Understand (for Evaluation / Code Review)

1. **Why use `NamedParameterJdbcTemplate` over raw JDBC?**
   - Eliminates ~15 lines of boilerplate per query (connection acquisition, try-with-resources, exception wrapping, ResultSet iteration). Named parameters (`:email`) are more readable and less error-prone than positional `?` placeholders.
2. **What is the difference between `DriverManagerDataSource` and `HikariDataSource`?**
   - `DriverManagerDataSource` creates a new connection on every `getConnection()` call — simple but slow. `HikariDataSource` maintains a pool of reusable connections — essential for production performance.
3. **Why does `UsersRepositoryJdbcTemplateImpl` accept `DataSource` instead of `JdbcTemplate`?**
   - It constructs its own `NamedParameterJdbcTemplate` internally. Accepting `DataSource` keeps the constructor signature consistent with `UsersRepositoryJdbcImpl` and matches the subject requirement.
4. **What does `<context:property-placeholder>` do?**
   - Registers a `PropertySourcesPlaceholderConfigurer` that resolves `${key}` placeholders in bean definitions against key-value pairs loaded from the specified `.properties` file.
