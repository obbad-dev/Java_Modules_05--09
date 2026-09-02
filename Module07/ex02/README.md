# Exercise 02: ORM

## Exercise Overview
The objective of **Exercise 02** is to implement a mini **Object-Relational Mapping (ORM)** framework using custom runtime annotations and Java reflection.

An ORM maps relational database tables and columns directly to Java classes and fields. Our mini ORM framework generates and prints the required SQL statements:
- Generating table creation DDL (`DROP TABLE IF EXISTS`, `CREATE TABLE`) on initialization.
- Generating `INSERT INTO` queries when saving an entity (`save(Object entity)`).
- Generating `UPDATE` queries when modifying an entity (`update(Object entity)`).
- Generating `SELECT` queries when fetching an entity by ID (`findById(Long id, Class<T> aClass)`).

---

## Concepts

1. **Object-Relational Mapping (ORM)**
   - Eliminates manual SQL writing by mapping domain models to database schemas using metadata.
2. **Runtime Annotations (`RetentionPolicy.RUNTIME`)**
   - Unlike `ex01`, ORM annotations must be retained at runtime so `OrmManager` can read them via reflection when executing operations.
3. **Type Mapping (Java to SQL)**
   - `String` -> `VARCHAR(length)`
   - `int` / `Integer` -> `INT` (or `SERIAL PRIMARY KEY` for IDs)
   - `long` / `Long` -> `BIGINT` (or `BIGINT PRIMARY KEY AUTO_INCREMENT` for IDs)
   - `boolean` / `Boolean` -> `BOOLEAN`
   - `Double` -> `DOUBLE`
4. **Dynamic SQL Query Construction**
   - Inspecting object field values via reflection (`field.setAccessible(true)`, `field.get(entity)`) to build parameterized or literal SQL strings dynamically.

---

## My Implementation

### Annotations (`src/main/java/annotations/`)
- `@OrmEntity(table = "...")`: Marks an entity class and defines its database table name.
- `@OrmColumn(name = "...", length = ...)`: Marks a field as a table column with an optional maximum length.
- `@OrmColumnId`: Marks the primary key field.

### Model Class (`src/main/java/models/User.java`)
```java
@OrmEntity(table = "simple_user")
public class User {
    @OrmColumnId
    private Long id;

    @OrmColumn(name = "first_name", length = 10)
    private String firstName;

    @OrmColumn(name = "last_name", length = 10)
    private String lastName;

    @OrmColumn(name = "age")
    private Integer age;
}
```

### The ORM Manager (`src/main/java/manager/OrmManager.java`)
- `buildCreateTableSql(Class<?> clazz)`:
  - Verifies class has `@OrmEntity`.
  - Generates `DROP TABLE IF EXISTS <table>;`
  - Iterates fields:
    - If `@OrmColumnId`: maps to primary key (e.g. `BIGINT PRIMARY KEY AUTO_INCREMENT`).
    - If `@OrmColumn`: maps type to SQL type (`VARCHAR(length)`, `INT`, etc.).
  - Closes table definition: `);`.
- `save(Object entity)`:
  - Generates and prints the `CREATE TABLE` DDL.
  - Iterates over fields marked with `@OrmColumn`.
  - Extracts column names and values via `field.get(entity)`.
  - Builds and prints the `INSERT INTO <table> (col1, col2) VALUES ('val1', val2);` statement.
- `update(Object entity)`:
  - Identifies `@OrmColumnId` field for the `WHERE id = ...` clause.
  - Iterates `@OrmColumn` fields, formatting `col = 'value'` or `col = NULL` if value is null.
  - Builds and prints the `UPDATE <table> SET ... WHERE id = ...;` statement.
- `findById(Long id, Class<T> aClass)`:
  - Collects all `@OrmColumnId` and `@OrmColumn` column names.
  - Builds and prints `SELECT id, first_name, last_name, age FROM <table> WHERE id = <id>;`.

### Application Demo (`src/main/java/app/Main.java`)
- Creates a `User(null, "oualid", "obbad", 23)`.
- Calls `orm.save(user)` and `orm.findById(1L, User.class)`.

---

## How It Works

1. `Main` instantiates `User` and `OrmManager`.
2. When `orm.save(user)` is called:
   - `OrmManager` inspects `user.getClass()`.
   - Reads `@OrmEntity(table = "simple_user")`.
   - Generates and prints `DROP TABLE IF EXISTS simple_user;` and `CREATE TABLE simple_user (...)`.
   - Reads all `@OrmColumn` fields on `user`, pulls their runtime values, and prints the `INSERT INTO` statement.
3. When `orm.findById(1L, User.class)` is called:
   - `OrmManager` inspects `User.class`, reads column names, and prints the `SELECT` query.

---

## How to Compile

```bash
cd Module07/ex02/ORM
mvn clean compile
```

---

## How to Run

```bash
mvn exec:java -Dexec.mainClass="app.Main"
```

Expected output:
```sql
DROP TABLE IF EXISTS simple_user;
CREATE TABLE simple_user (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
first_name VARCHAR(10),
last_name VARCHAR(10),
age INT);
INSERT INTO simple_user(first_name, last_name, age)
 VALUES 
('oualid', 'obbad', 23);
SELECT id, first_name, last_name, age FROM simple_user WHERE id = 1;
```

---

## Important Things to Understand (for Evaluation / Code Review)

1. **Why must ORM annotations have `RetentionPolicy.RUNTIME`?**
   - The ORM framework discovers table and column mappings dynamically while the program runs. If retention was `SOURCE` or `CLASS`, the metadata would not be visible via `Class.getAnnotation()`.
2. **How does `OrmManager` handle `null` values in `update()`?**
   - The subject requires that update replaces values in columns specified in the entity even if object field value is `null`. The implementation binds `field.get(entity)` directly into the SQL string or sets `NULL`.
3. **What is the fundamental difference between `ex01` and `ex02`?**
   - `ex01` uses compile-time annotation processing to generate code before execution.
   - `ex02` uses runtime reflection and annotations to execute dynamic behavior while the application is running.
