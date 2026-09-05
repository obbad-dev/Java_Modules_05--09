# Module 08 – Exercise 01: JdbcTemplate

## Guide & Step-by-Step Plan

---

## Part 1 — Concepts You Need to Understand

### 1. What is JDBC?

JDBC (Java Database Connectivity) is Java's standard API for talking to databases. In plain terms:

- You open a **connection** to a database (like PostgreSQL).
- You create a **Statement** (a SQL query wrapped in a Java object).
- You **execute** the statement and get back a **ResultSet** — a table of rows and columns.
- You loop through the ResultSet, reading data row by row, column by column.
- You **close** everything (connection, statement, result set) when you're done.

The problem with raw JDBC is that it requires a lot of repetitive boilerplate code: opening connections, handling exceptions, closing resources, etc. Every single query looks almost identical in structure.

### 2. What is JdbcTemplate?

`JdbcTemplate` is a Spring class that **wraps raw JDBC** and removes all the boilerplate. It handles:

- Opening and closing connections for you
- Creating and closing statements
- Iterating over result sets
- Catching and translating SQL exceptions

You just provide the SQL query and tell it how to map each row to a Java object. Everything else is automatic.

### 3. What is NamedParameterJdbcTemplate?

A variant of JdbcTemplate where instead of using `?` placeholders in SQL:

```
SELECT * FROM users WHERE id = ?
```

…you use **named parameters** like `:id`:

```
SELECT * FROM users WHERE id = :id
```

This is more readable and less error-prone when you have many parameters.

### 4. What is a DataSource?

A `DataSource` is an object that knows how to create database connections. Instead of manually creating connections with URLs, usernames, and passwords everywhere, you configure a DataSource once and pass it around.

Two types are mentioned in this exercise:

- **DriverManagerDataSource** — A simple Spring-provided DataSource. Creates a new connection every time one is requested. Simple but not efficient for production.
- **HikariDataSource** — A high-performance **connection pool**. It keeps a pool of pre-opened connections and reuses them, which is much faster. Hikari is the most popular Java connection pool library.

Both implement the same `DataSource` interface, so your repository code doesn't care which one it receives.

### 5. What is a RowMapper?

A `RowMapper` is a small piece of logic that tells JdbcTemplate **how to convert one row** of a ResultSet into a Java object. For each row returned by a query, the RowMapper reads the columns and creates the corresponding object (e.g., a `User`).

### 6. What is the Repository Pattern?

The repository pattern separates your data access logic from your business logic. A **repository** is a class whose only job is to interact with the database for a specific entity (like `User`). It provides methods like `findById`, `save`, `update`, `delete`, etc.

### 7. What is a CrudRepository?

CRUD stands for **Create, Read, Update, Delete** — the four basic database operations. A `CrudRepository` is a generic interface that declares these operations. By making it generic (`CrudRepository<T>`), you can reuse the same interface for any entity type.

### 8. What is Spring XML Configuration (context.xml)?

In Spring, a **context.xml** file is where you declare your **beans** (objects managed by Spring) and their dependencies using XML. You define:

- What classes to instantiate
- What constructor arguments or properties to set
- How beans reference each other

Spring reads this file and creates all the objects for you, wiring them together automatically.

### 9. What are Property Placeholders?

Instead of hardcoding database URLs, usernames, and passwords in your XML, you put them in a separate `.properties` file and reference them using `${property.name}` syntax. Spring replaces these placeholders with actual values at startup. This separates configuration from code.

### 10. What is ApplicationContext?

The `ApplicationContext` is Spring's container — it reads your configuration (XML, annotations, etc.), creates all the beans, injects dependencies, and gives you a way to retrieve beans by name or type. It's the central piece that ties everything together.

---

## Part 2 — What You Need to Create

Here is every file you need, organized by the required project structure:

### Files to Create or Modify

| # | File | Type | Status |
|---|------|------|--------|
| 1 | `pom.xml` | Maven config | **Modify** (add dependencies) |
| 2 | `models/User.java` | Model class | **Already exists** ✅ |
| 3 | `repositories/CrudRepository.java` | Generic interface | **Modify** (currently an empty class) |
| 4 | `repositories/UsersRepository.java` | Interface | **Create** |
| 5 | `repositories/UsersRepositoryJdbcImpl.java` | Class | **Create** |
| 6 | `repositories/UsersRepositoryJdbcTemplateImpl.java` | Class | **Create** |
| 7 | `application/Main.java` | Entry point | **Modify** |
| 8 | `resources/context.xml` | Spring config | **Create** |
| 9 | `resources/db.properties` | DB credentials | **Create** |

---

## Part 3 — Step-by-Step Plan

### Step 0: Set Up PostgreSQL

Before any Java code, make sure you have:
- A running PostgreSQL server
- A database created (e.g., `database`)
- A `users` table with at least `id` (BIGINT/SERIAL) and `email` (VARCHAR) columns
- Some test data inserted into the table

### Step 1: Update `pom.xml`

You need to add Maven dependencies for:
- **Spring Context** — for `ApplicationContext`, `ClassPathXmlApplicationContext`, and XML bean configuration
- **Spring JDBC** — for `JdbcTemplate`, `NamedParameterJdbcTemplate`, and `DriverManagerDataSource`
- **PostgreSQL Driver** — the JDBC driver that lets Java talk to PostgreSQL
- **HikariCP** — for the `HikariDataSource` connection pool

> [!TIP]
> Search for these artifacts on Maven Central:
> `spring-context`, `spring-jdbc`, `postgresql`, `HikariCP`

### Step 2: Transform `CrudRepository` into a Generic Interface

The existing [CrudRepository.java](file:///home/oobbad/Desktop/pool_java_part2/Module08/ex01/Service/src/main/java/_42/spring/service/repositories/CrudRepository.java) is currently an empty class. You need to:

- Change it from a `class` to an `interface`
- Make it **generic** with a type parameter `<T>`
- Declare the five CRUD methods:
  - `findById(Long id)` → returns `T`
  - `findAll()` → returns `List<T>`
  - `save(T entity)` → returns `void`
  - `update(T entity)` → returns `void`
  - `delete(Long id)` → returns `void`

### Step 3: Create `UsersRepository` Interface

Create a new interface that:
- Extends `CrudRepository<User>`
- Adds one extra method: `findByEmail(String email)` → returns `Optional<User>`

This interface inherits all five CRUD methods from `CrudRepository` and adds the email lookup.

### Step 4: Create `UsersRepositoryJdbcImpl`

This is the **manual JDBC** implementation. It should:

- Implement `UsersRepository`
- Accept a `DataSource` in its **constructor**
- Implement every method using **raw JDBC**: get a connection from the DataSource, create a `PreparedStatement`, execute it, read the `ResultSet`, map rows to `User` objects, and close resources
- Handle SQL exceptions properly

**What each method does:**
| Method | SQL Operation |
|--------|--------------|
| `findById` | `SELECT ... WHERE id = ?` |
| `findAll` | `SELECT ...` (all rows) |
| `findByEmail` | `SELECT ... WHERE email = ?` |
| `save` | `INSERT INTO ...` |
| `update` | `UPDATE ... WHERE id = ?` |
| `delete` | `DELETE FROM ... WHERE id = ?` |

### Step 5: Create `UsersRepositoryJdbcTemplateImpl`

This is the **JdbcTemplate** implementation. It should:

- Implement `UsersRepository`
- Accept a `DataSource` in its **constructor**
- Create a `JdbcTemplate` or `NamedParameterJdbcTemplate` from the DataSource
- Implement every method using JdbcTemplate's helper methods (like `query`, `queryForObject`, `update`)
- Use a **RowMapper** to convert ResultSet rows into `User` objects

**Key difference from Step 4:** No manual connection/statement/resultset management. JdbcTemplate does all of that for you. Your methods will be significantly shorter and cleaner.

### Step 6: Create `db.properties`

Create a properties file in `src/main/resources/` containing your database connection details:
- Database URL (JDBC format for PostgreSQL)
- Username
- Password
- Driver class name

The subject gives you the exact property key names to use:
`db.url`, `db.user`, `db.password`, `db.driver.name`

### Step 7: Create `context.xml`

Create the Spring XML configuration in `src/main/resources/`. This file must:

1. **Import `db.properties`** using a property placeholder configurer, so you can use `${db.url}`, `${db.user}`, etc.

2. **Declare two DataSource beans:**
   - One bean of type `DriverManagerDataSource` — set its `url`, `username`, `password`, and `driverClassName` properties using `${...}` placeholders
   - One bean of type `HikariDataSource` — same idea, but with Hikari's property names (check Hikari documentation for the correct setter names: `jdbcUrl`, `username`, `password`, `driverClassName`)

3. **Declare two repository beans:**
   - `usersRepositoryJdbc` → class is `UsersRepositoryJdbcImpl`, inject one of the DataSource beans via constructor
   - `usersRepositoryJdbcTemplate` → class is `UsersRepositoryJdbcTemplateImpl`, inject the other DataSource bean via constructor

> [!IMPORTANT]
> The bean IDs must match exactly what the Main class uses:
> `"usersRepositoryJdbc"` and `"usersRepositoryJdbcTemplate"`

### Step 8: Modify `Main.java`

Rewrite [Main.java](file:///home/oobbad/Desktop/pool_java_part2/Module08/ex01/Service/src/main/java/_42/spring/service/application/Main.java) to:

1. Create an `ApplicationContext` by loading `context.xml` using `ClassPathXmlApplicationContext`
2. Get the first repository bean (`"usersRepositoryJdbc"`) and call `findAll()`, print the result
3. Get the second repository bean (`"usersRepositoryJdbcTemplate"`) and call `findAll()`, print the result

The subject shows you the exact code for this step.

---

## Part 4 — How Everything Works Together

```
┌─────────────────┐
│   db.properties │──── Database credentials
└────────┬────────┘
         │ referenced by
         ▼
┌─────────────────┐     creates      ┌──────────────────────┐
│   context.xml   │────────────────►  │  DataSource beans    │
│                 │                   │  (DriverManager &    │
│  (Spring XML    │                   │   HikariDataSource)  │
│   config)       │                   └──────────┬───────────┘
│                 │                              │ injected into
│                 │     creates      ┌───────────▼───────────┐
│                 │────────────────► │  Repository beans     │
└─────────────────┘                  │  (JdbcImpl &          │
                                     │   JdbcTemplateImpl)   │
                                     └───────────┬───────────┘
                                                 │ used by
                                     ┌───────────▼───────────┐
                                     │      Main.java        │
                                     │  (loads context,      │
                                     │   gets beans,         │
                                     │   calls findAll)      │
                                     └───────────┬───────────┘
                                                 │ talks to
                                     ┌───────────▼───────────┐
                                     │    PostgreSQL DB      │
                                     │  (users table)        │
                                     └───────────────────────┘
```

**Flow at runtime:**
1. `Main` loads `context.xml` → Spring creates the `ApplicationContext`
2. Spring reads `db.properties` and resolves all `${...}` placeholders
3. Spring creates two `DataSource` beans with the DB connection info
4. Spring creates two repository beans, injecting a `DataSource` into each via constructor
5. `Main` retrieves each repository bean and calls `findAll()`
6. Each repository uses its own approach (raw JDBC vs JdbcTemplate) to query the `users` table
7. Both return the same list of `User` objects, printed to the console

---

## Part 5 — Checklist Before You Start Coding

- [ ] I understand the difference between raw JDBC and JdbcTemplate
- [ ] I know what a `DataSource` is and why there are two types
- [ ] I know what a `RowMapper` does
- [ ] I understand Java generics enough to create `CrudRepository<T>`
- [ ] I understand how Spring XML configuration (`context.xml`) declares and wires beans
- [ ] I know how property placeholders (`${...}`) work with `.properties` files
- [ ] I have PostgreSQL running with a `users` table created
- [ ] I know which Maven dependencies I need to add

> [!NOTE]
> The two repository implementations should produce **identical results**. The only difference is *how* they talk to the database internally. This exercise is specifically designed to show you the value of JdbcTemplate by making you implement the same thing twice — once the hard way (raw JDBC) and once the easy way (JdbcTemplate).
