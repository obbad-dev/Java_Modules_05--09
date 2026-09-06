# Implementation Plan: `db.properties` & `context.xml`

This guide explains the concepts, requirements, and step-by-step strategy for configuring Spring XML and database properties for Exercise 01. **No direct solution code is provided** so you can implement it independently.

---

## 1. Overview of the Goal

The objective is to replace the manual `HikariConfig` / `HikariDataSource` instantiation currently inside `Main.java` with Spring's **Inversion of Control (IoC)** container.

Spring will:
1. Load database credentials from an external `db.properties` file.
2. Instantiate two different `DataSource` beans using those credentials.
3. Instantiate two `UsersRepository` beans, injecting a DataSource into each via constructor.
4. Provide the fully wired repository beans to `Main.java` via `ApplicationContext`.

```
src/main/resources/db.properties (DB Credentials)
               │
               ▼
src/main/resources/context.xml   (Spring Bean Definitions)
       ├── 1. Reads db.properties via placeholder configurer
       ├── 2. Creates DataSource bean A: DriverManagerDataSource
       ├── 3. Creates DataSource bean B: HikariDataSource
       ├── 4. Creates Repository bean A: UsersRepositoryJdbcImpl (injects DataSource A)
       └── 5. Creates Repository bean B: UsersRepositoryJdbcTemplateImpl (injects DataSource B)
               │
               ▼
src/main/java/.../Main.java      (Retrieves repositories and calls findAll())
```

---

## 2. Pre-requisite: Maven Dependencies Check

Before starting, check your `pom.xml`:
- In Exercise 00, you used Spring Context (`spring-context`).
- In Exercise 01, `pom.xml` currently has `spring-jdbc`, `postgresql`, and `HikariCP`.
- To use `ApplicationContext` and `ClassPathXmlApplicationContext` (as required by the subject for `Main.java`), you must ensure **`spring-context`** is present in your `pom.xml`.

---

## 3. Part 1: Implementing `db.properties`

### Location
Create this file at:
`src/main/resources/db.properties`

### Concepts & Purpose
A `.properties` file holds key-value pairs formatted as `key=value`. This decouples environment-specific settings (database URLs, passwords, usernames) from Java source code and XML configurations.

### Requirements from the Subject
The subject specifies four exact property keys that must be present:
- `db.url` — The JDBC connection string to your PostgreSQL database (e.g. `jdbc:postgresql://<host>:<port>/<dbname>`).
- `db.user` — The database username.
- `db.password` — The database password.
- `db.driver.name` — The fully qualified class name of the PostgreSQL JDBC driver.

### Your Task
- Identify your local PostgreSQL credentials (database name, port, user, password).
- Find the canonical driver class name for PostgreSQL (you already imported `org.postgresql:postgresql`).
- Write out the 4 key-value pairs matching the exact keys given above.

---

## 4. Part 2: Implementing `context.xml`

### Location
Create this file at:
`src/main/resources/context.xml`

### Section A: XML Root and Namespaces
A standard Spring bean XML file starts with `<beans>` as the root tag.
- To use property placeholders (e.g., `${db.url}`), Spring needs a mechanism to resolve `${...}` expressions.
- You have two ways in Spring:
  - **Option 1 (XML Namespace):** Add `xmlns:context` to the `<beans>` tag and use `<context:property-placeholder location="classpath:db.properties"/>`.
  - **Option 2 (Standard Bean):** Declare a bean of class `org.springframework.context.support.PropertySourcesPlaceholderConfigurer` or `org.springframework.beans.factory.config.PropertyPlaceholderConfigurer` and set its `location` property to `classpath:db.properties`.

*Tip: Choose whichever approach you find cleaner or are more familiar with from Spring documentation.*

---

### Section B: Declaring the Two DataSource Beans

The subject requires two distinct `DataSource` implementations to be registered as beans:

#### 1. `DriverManagerDataSource`
- **Class:** `org.springframework.jdbc.datasource.DriverManagerDataSource` (provided by `spring-jdbc`).
- **Bean ID:** Choose a descriptive name (e.g., `driverManagerDataSource`).
- **Configuration Mechanism:** It uses standard JavaBean setter properties via `<property name="..." value="${...}"/>`.
- **Properties to configure:** Look at the setter methods of `DriverManagerDataSource`:
  - URL property name (e.g., `url`)
  - Username property name (e.g., `username`)
  - Password property name (e.g., `password`)
  - Driver class name property name (e.g., `driverClassName`)

#### 2. `HikariDataSource`
- **Class:** `com.zaxxer.hikari.HikariDataSource` (provided by `HikariCP`).
- **Bean ID:** Choose a descriptive name (e.g., `hikariDataSource`).
- **Configuration Mechanism:** Configured via `<property name="..." value="${...}"/>`.
- **Important Gotcha:** Hikari's property names are slightly different from Spring's `DriverManagerDataSource`:
  - Check what setter Hikari uses for the JDBC URL (Hint: Hikari uses `jdbcUrl`, not `url`).
  - Check the setters for `username`, `password`, and `driverClassName`.

---

### Section C: Declaring the Two Repository Beans

The subject specifies the **exact bean IDs** you must use:
1. `usersRepositoryJdbc`
2. `usersRepositoryJdbcTemplate`

#### Requirements:
1. **Bean 1 (`usersRepositoryJdbc`):**
   - Class: `_42.spring.service.repositories.UsersRepositoryJdbcImpl`
   - Constructor argument: Needs a `DataSource`. Use `<constructor-arg ref="..."/>` pointing to one of your DataSource beans (e.g., `driverManagerDataSource`).

2. **Bean 2 (`usersRepositoryJdbcTemplate`):**
   - Class: `_42.spring.service.repositories.UsersRepositoryJdbcTemplateImpl`
   - Constructor argument: Needs a `DataSource`. Use `<constructor-arg ref="..."/>` pointing to the other DataSource bean (e.g., `hikariDataSource`).

---

## 5. Part 3: Updating `Main.java` for Testing

The subject shows the exact demonstration required in `Main.java`:

1. Load the Spring context:
   ```java
   ApplicationContext context = new ClassPathXmlApplicationContext("context.xml");
   ```
2. Retrieve `usersRepositoryJdbc` by its bean name and call `findAll()`, printing the result.
3. Retrieve `usersRepositoryJdbcTemplate` by its bean name and call `findAll()`, printing the result.
4. Verify that both repositories connect successfully and output identical lists of users from the database.

---

## 6. Self-Verification Checklist

Before running your code, check off these points:

- [ ] `spring-context` is listed in `pom.xml` dependencies and builds without errors.
- [ ] `db.properties` is in `src/main/resources/` with the exact keys: `db.url`, `db.user`, `db.password`, `db.driver.name`.
- [ ] `context.xml` is in `src/main/resources/`.
- [ ] Property placeholder is configured to read `db.properties`.
- [ ] The `DriverManagerDataSource` bean is configured with `${...}` placeholders.
- [ ] The `HikariDataSource` bean is configured with `${...}` placeholders (watching out for `jdbcUrl`).
- [ ] Repository bean IDs match the subject requirements: `usersRepositoryJdbc` and `usersRepositoryJdbcTemplate`.
- [ ] Both repository beans receive a `DataSource` bean reference via `<constructor-arg ref="..."/>`.
- [ ] `Main.java` retrieves both beans using `context.getBean(...)` and prints `findAll()`.
- [ ] Running `Main` executes both queries against PostgreSQL and prints the users without any exceptions.
