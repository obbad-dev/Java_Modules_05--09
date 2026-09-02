# Java Module 06 – JUnit / Mockito

## Overview

Java Module 06 introduces professional **software testing methodologies** in Java. Writing production code is only half the engineering challenge; ensuring that code is resilient, testable, regression-free, and isolated requires automated testing frameworks.

This module covers the modern test stack:
- **JUnit 5 (Jupiter)**: The standard Java testing framework.
- **Parameterized & Data-Driven Testing**: Testing multiple inputs using `@ValueSource` and `@CsvFileSource`.
- **In-Memory Integration Testing**: Using **HSQLDB** and Spring JDBC's `EmbeddedDatabaseBuilder` to test persistence layers without external database dependencies.
- **Mocking & Isolation with Mockito**: Using `@Mock`, `@InjectMocks`, stubbing (`when().thenReturn()`), and interaction verification (`verify()`) to test business logic in total isolation from data stores.

---

## Main Programming Concepts Introduced

1. **The Test Pyramid & Test Isolation**
   - Unit tests verify small, isolated units of execution fast and deterministically.
   - Integration tests verify that components (e.g. Repositories) interact correctly with databases.
   - Tests must have no side effects on each other (`@BeforeEach` ensures clean state).

2. **Parameterized Testing**
   - Avoiding repetitive test code by feeding parameters into a single test method.
   - Using `@ValueSource` for primitive literals and `@CsvFileSource` for external tabular datasets.

3. **In-Memory Database for Testing (HSQLDB)**
   - Testing against production databases (like PostgreSQL) is slow, fragile, and requires network setup.
   - Embedded databases run inside the JVM process, spin up in milliseconds, and reset between tests.

4. **Repository Testing (CRUD Verification)**
   - Verifying all standard operations (`findAll`, `findById`, `save`, `update`, `delete`) against real SQL execution in memory.

5. **Test Doubles & Mocking (Mockito)**
   - Isolating the unit under test (the Service layer) from its dependencies (the Repository).
   - Stubs: Simulating dependency behavior with pre-canned answers.
   - Spies & Verification: Confirming that expected methods were called with expected arguments.

---

## Why These Concepts Are Important

- **Confidence & Refactoring**: Automated test suites allow developers to refactor with confidence that existing features will not break.
- **Speed & Portability**: CI/CD pipelines run thousands of tests on clean runner environments where installing heavy DBMS software is impractical. Embedded DBs and mocks make tests fast and self-contained.
- **Boundary Separation**: If a service test fails, you want to know immediately whether the service logic failed, not whether a database connection dropped. Mocking isolates the fault domain.

---

## Main Exercises Covered

| Exercise | Name | Testing Level | Technologies & Tools |
| :--- | :--- | :--- | :--- |
| **ex00** | First Tests | Unit Testing (Algorithmic) | JUnit 5, `@ParameterizedTest`, `@ValueSource`, `@CsvFileSource`, CSV parsing |
| **ex01** | Embedded DataBase | Integration Testing (Setup) | Spring JDBC `EmbeddedDatabaseBuilder`, HSQLDB, `@BeforeEach` |
| **ex02** | Test for JDBC Repository | Integration Testing (Persistence) | JDBC CRUD, `ProductsRepositoryJdbcImpl`, Predefined test fixtures |
| **ex03** | Test for Service | Unit Testing (Business Logic) | Mockito (`@Mock`, `@InjectMocks`), Stubbing (`when`), Verification (`verify`) |

---

## What You Should Learn and Understand

1. The difference between unit tests (isolated, fast) and integration tests (wiring components together).
2. How to write parameterized tests in JUnit 5 to achieve high test coverage with minimal code duplication.
3. How to use `EmbeddedDatabaseBuilder` to spin up an in-memory SQL database from `schema.sql` and `data.sql` before each test.
4. How to verify that a repository correctly generates IDs and mutates database state.
5. How and why to use Mockito to simulate repositories when testing business logic services.

---

## How Concepts Are Used in My Implementation

- **Data-Driven Unit Tests**: In `ex00`, `NumberWorkerTest` verifies primes, composites, and invalid inputs using `@ValueSource`, and tests digit sum calculations against `data.csv`.
- **Database Reset per Test**: In `ex01` and `ex02`, `EmbeddedDataSourceTest` and `ProductsReposutoryJdbcImplTest` rebuild an HSQLDB instance before every test in `@BeforeEach`, guaranteeing zero cross-test interference.
- **Full CRUD Verification**: In `ex02`, `ProductsRepositoryJdbcImplTest` compares database outputs against immutable constants (`EXPECTED_FIND_ALL_PRODUCTS`, etc.).
- **Mock-Driven Service Testing**: In `ex03`, `UsersServiceImplTest` uses Mockito annotations (`@Mock`, `@InjectMocks`) to mock `UsersRepository` and verify that `usersRepository.update(user)` is executed when credentials match.

---

## Module Directory Structure

```text
Module06/
├── .gitignore
├── README.md
├── ex00/
│   ├── README.md
│   └── Tests/
│       ├── pom.xml
│       └── src/
│           ├── main/java/fr/s42/numbers/ (NumberWorker, IllegalNumberException)
│           └── test/
│               ├── java/fr/s42/numbers/ (NumberWorkerTest)
│               └── resources/ (data.csv)
├── ex01/
│   ├── README.md
│   └── Tests/
│       ├── pom.xml
│       └── src/test/
│           ├── java/fr/s42/repositories/ (EmbeddedDataSourceTest)
│           └── resources/ (schema.sql, data.sql)
├── ex02/
│   ├── README.md
│   └── Tests/
│       ├── pom.xml
│       └── src/
│           ├── main/java/fr/s42/ (models/Product, repositories/ProductsRepository...)
│           └── test/java/fr/s42/repositories/ (ProductsReposutoryJdbcImplTest)
└── ex03/
    ├── README.md
    └── Tests/
        ├── pom.xml
        └── src/
            ├── main/java/fr/s42/ (services/UsersServiceImpl, exceptions, models)
            └── test/java/fr/s42/services/ (UsersServiceImplTest)
```
