# Exercise 02: Test for JDBC Repository

## Exercise Overview
The objective of **Exercise 02** is to implement a JDBC repository for products and verify all its CRUD operations using automated integration tests against the embedded HSQLDB database built in Exercise 01.

We implement:
- `Product` domain model (`id`, `name`, `price`).
- `ProductsRepository` interface.
- `ProductsRepositoryJdbcImpl` repository implementation.
- `ProductsReposutoryJdbcImplTest` checking all operations against predefined expected fixtures.

---

## Concepts

1. **Complete CRUD Operations via JDBC**
   - **Create**: `save(Product product)` inserts a new record and assigns the generated primary key.
   - **Read**: `findAll()` returns all products; `findById(Long id)` returns an `Optional<Product>`.
   - **Update**: `update(Product product)` modifies an existing record.
   - **Delete**: `delete(Long id)` removes a record by ID.
2. **Integration Testing of Repositories**
   - Testing real SQL queries (`SELECT`, `INSERT`, `UPDATE`, `DELETE`) against a real database engine to catch syntax and mapping errors before production.
3. **Test Fixtures & Immutable Expectations**
   - Defining constant expected models (`EXPECTED_FIND_ALL_PRODUCTS`, `EXPECTED_FIND_BY_ID_PRODUCT`, `EXPECTED_UPDATED_PRODUCT`) to compare against repository outputs.

---

## My Implementation

### Source Classes (`src/main/java/fr/s42/`)
- `Product.java`: Contains `id` (`Long`), `name` (`String`), `price` (`BigDecimal`), with constructor, getters, setters, `equals`, `hashCode`, and `toString`.
- `ProductsRepository.java`: Contract interface with CRUD methods.
- `ProductsRepositoryJdbcImpl.java`:
  - Uses `PreparedStatement` and `try-with-resources`.
  - `findAll()`: Queries `SELECT * FROM product ORDER BY id;`.
  - `findById(Long id)`: Queries `SELECT id, name, price FROM product WHERE id = ?`.
  - `update(Product product)`: Executes `UPDATE product SET name = ?, price = ? WHERE id = ?`.
  - `save(Product product)`: Executes `INSERT INTO product (name, price) VALUES (?, ?)` with `Statement.RETURN_GENERATED_KEYS`, assigning the new ID to the entity.
  - `delete(Long id)`: Executes `DELETE FROM product WHERE id = ?;`.

### Test Suite (`ProductsReposutoryJdbcImplTest.java`)
- Initialized before each test via `@BeforeEach` with fresh HSQLDB data.
- Test Cases:
  - `testFindAll()`: Asserts repository returns the exact list of 5 expected products.
  - `testFindByIdTrueId()`: Asserts searching for ID `1L` returns the expected product.
  - `testFindByIdFakeId()`: Asserts searching for non-existent ID `99L` returns `Optional.empty()`.
  - `testUpdate()`: Updates ID `1L` to `Wireless Mouse Pro` / `43.66`, then calls `findById(1L)` to confirm persistence.
  - `testSave()`: Saves a new product (`Google pixel 7`), verifies non-null generated ID, and retrieves it via `findById`.
  - `testDelete()`: Deletes ID `1L` and asserts `findById(1L).isEmpty()`.

---

## How It Works

1. `@BeforeEach` initializes a new HSQLDB database containing the 5 default products from `data.sql`.
2. A test method executes (for example, `testDelete(1L)`).
3. The repository runs `DELETE FROM product WHERE id = 1`.
4. The test calls `findById(1L)` and verifies the item is gone.
5. Even though ID `1L` was deleted, the next test (`testFindAll`) gets a brand-new database instance where ID `1L` is restored.

---

## How to Compile

```bash
cd Module06/ex02/Tests
mvn clean compile
```

---

## How to Run

```bash
mvn test
```

To run specifically this test class:
```bash
mvn test -Dtest=ProductsReposutoryJdbcImplTest
```

---

## Important Things to Understand (for Evaluation / Code Review)

1. **Why use `BigDecimal` for prices instead of `double` or `float`?**
   - Binary floating-point types cannot accurately represent base-10 decimals (e.g. `0.1 + 0.2 != 0.3`), which leads to rounding errors in financial and currency calculations.
2. **Why call `findById` inside `testUpdate` and `testSave`?**
   - To verify that the change was actually committed and persisted in the underlying database table, not merely held in memory.
