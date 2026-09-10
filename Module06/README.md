<div align="center">

# 🧪 Java Module 06 – JUnit 5 & Mockito

**Automated Testing, Test Pyramid, Parameterized Tests, In-Memory HSQLDB & Mocking Frameworks**

![Java](https://img.shields.io/badge/Java-25-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![JUnit 5](https://img.shields.io/badge/JUnit_5-Jupiter-25A162?style=for-the-badge&logo=junit5&logoColor=white)
![Mockito](https://img.shields.io/badge/Mockito-Mocking_Framework-C53A3A?style=for-the-badge)
![HSQLDB](https://img.shields.io/badge/HSQLDB-In--Memory_DB-00599C?style=for-the-badge)
![Testing](https://img.shields.io/badge/Test_Pyramid-Unit_&_Integration-6A1B9A?style=for-the-badge)
![Maven](https://img.shields.io/badge/Maven-Surefire-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)

---

*From Unit Verification → Embedded Database Integration → Isolated Mocking*

</div>

---

## 📖 Overview

Java Module 06 introduces professional **software verification and automated testing** in Java. Writing implementation code is only half of software engineering; verifying that code is correct, regression-free, isolated, and resilient requires rigorous testing frameworks.

This module explores the full testing pyramid through modern industry standards:
- **Unit Testing (JUnit 5 Jupiter)**: Deterministic verification of core algorithms.
- **Parameterized & Data-Driven Tests**: High-coverage test suites driven by `@ValueSource` and CSV datasets via `@CsvFileSource`.
- **In-Memory Integration Testing**: Ephemeral embedded databases using **HSQLDB** and Spring's `EmbeddedDatabaseBuilder` to verify SQL persistence without external database servers.
- **Test Doubles & Mocking (Mockito)**: Decoupling business logic from data stores using `@Mock`, `@InjectMocks`, stubbing (`when(...).thenReturn(...)`), and interaction assertions (`verify(...)`).

> [!IMPORTANT]
> A well-architected test suite runs deterministically in any CI/CD pipeline without external environment dependencies. In-memory databases eliminate slow, fragile database networks, while mocking isolates faults to the exact class under test.

---

## 🗺️ Module Progression

```mermaid
flowchart LR
    subgraph EX00["🟢 Exercise 00"]
        A["Unit Testing\nNumberWorker"]
        A1["Parameterized Tests\n@ValueSource + CSV"]
        A --> A1
    end

    subgraph EX01["🟡 Exercise 01"]
        B["Embedded DB Setup\nEmbeddedDatabaseBuilder"]
        B1["HSQLDB Engine\nschema.sql + data.sql"]
        B --> B1
    end

    subgraph EX02["🟠 Exercise 02"]
        C["JDBC Repo Integration\nProductsRepository"]
        C1["Full CRUD Tests\n@BeforeEach Rebuild"]
        C --> C1
    end

    subgraph EX03["🔴 Exercise 03"]
        D["Mockito Isolation\nUsersServiceImpl"]
        D1["Mocking & Verify\nwhen / thenReturn / verify"]
        D --> D1
    end

    EX00 ==>|"Add Persistence"| EX01 ==>|"Full Repo CRUD"| EX02 ==>|"Mock Persistence"| EX03

    style EX00 fill:#d4edda,stroke:#28a745,color:#000
    style EX01 fill:#fff3cd,stroke:#ffc107,color:#000
    style EX02 fill:#ffe5d0,stroke:#fd7e14,color:#000
    style EX03 fill:#f8d7da,stroke:#dc3545,color:#000
```

---

## 🔺 The Testing Pyramid in Module 06

```mermaid
flowchart TD
    subgraph PYRAMID["Test Granularity & Scope"]
        UNIT["⚡ Unit Tests (ex00 & ex03)\nFast • Isolated • In-Memory\nJUnit 5 + Mockito Stubs"]
        INTEG["🔌 Integration Tests (ex01 & ex02)\nReal SQL Execution • HSQLDB\nEmbeddedDatabaseBuilder"]
    end

    UNIT --- INTEG

    style UNIT fill:#c8e6c9,stroke:#2e7d32,stroke-width:2px,color:#000
    style INTEG fill:#bbdefb,stroke:#1565c0,stroke-width:2px,color:#000
```

---

## 🎭 Mockito Stubbing & Verification Lifecycle (ex03)

```mermaid
sequenceDiagram
    autonumber
    actor Tester as UsersServiceImplTest
    participant Svc as UsersServiceImpl (Target)
    participant Mock as UsersRepository (Mock Double)

    Tester->>Mock: when(findByLogin("alice")).thenReturn(Optional.of(user))
    Note over Tester, Mock: 1. Stub the mock repository with simulated return value
    Tester->>Svc: authenticate("alice", "correct_pwd")
    Svc->>Mock: findByLogin("alice")
    Mock-->>Svc: Optional<User> (Stubbed value returned)
    Svc->>Svc: Verify password matches
    Svc->>Mock: update(user) [Sets authenticated = true]
    Svc-->>Tester: Returns true
    Tester->>Mock: verify(usersRepository).update(user)
    Note over Tester, Mock: 2. Assert that update was invoked exactly once
```

---

## 📋 Concepts Breakdown by Exercise

| Testing Concept / Tool | ex00 | ex01 | ex02 | ex03 |
| :--- | :---: | :---: | :---: | :---: |
| `@Test` Basic Assertions (`assertEquals`, `assertTrue`) | ✅ | ✅ | ✅ | ✅ |
| Exception Verification (`assertThrows`) | ✅ | | | ✅ |
| Parameterized Tests (`@ParameterizedTest`) | ✅ | | | |
| Primitive Value Source (`@ValueSource`) | ✅ | | | |
| External Dataset Source (`@CsvFileSource`) | ✅ | | | |
| In-Memory DB Lifecycle (`EmbeddedDatabaseBuilder`) | | ✅ | ✅ | |
| Clean Database State per Test (`@BeforeEach`) | | ✅ | ✅ | |
| Full CRUD Persistence Verification | | | ✅ | |
| Mock Injection (`@Mock`, `@InjectMocks`, Mockito) | | | | ✅ |
| Stubbing Method Behavior (`when(...).thenReturn(...)`) | | | | ✅ |
| Interaction Verification (`verify(mock).method(...)`) | | | | ✅ |

---

## 🎯 Exercises Overview

<table>
<tr>
<td width="25%" valign="top">

### 🟢 ex00
**First Tests**

```text
NumberWorker
     ↓
@ParameterizedTest
  ├── @ValueSource (Primes)
  └── @CsvFileSource (DigitSum)
     ↓
assertThrows(IllegalNumber...)
```

**Key Deliverables:**
- `NumberWorker.java`
- `NumberWorkerTest.java`
- `data.csv`

**Takeaway:**
> Parameterized tests eliminate copy-pasting test methods for different input cases.

</td>
<td width="25%" valign="top">

### 🟡 ex01
**Embedded DataBase**

```text
schema.sql + data.sql
     ↓
EmbeddedDatabaseBuilder
     ↓ (Type.HSQL)
In-Memory DataSource
     ↓
getConnection() != null
```

**Key Deliverables:**
- `EmbeddedDataSourceTest`
- `schema.sql`
- `data.sql`

**Takeaway:**
> Embedded databases boot in milliseconds and require zero host DBMS installation.

</td>
<td width="25%" valign="top">

### 🟠 ex02
**Test for JDBC Repo**

```text
@BeforeEach (Rebuild DB)
     ↓
ProductsRepositoryJdbcImpl
  ├── findById()
  ├── update()
  ├── save() (assert ID)
  └── delete()
```

**Key Deliverables:**
- `ProductsRepositoryJdbcImpl`
- `ProductsReposutoryJdbcImplTest`
- Expected fixture constants

**Takeaway:**
> Every test must execute against a clean, freshly-seeded database state.

</td>
<td width="25%" valign="top">

### 🔴 ex03
**Test for Service**

```text
@Mock UsersRepository
     ↓
@InjectMocks UsersServiceImpl
     ↓
when(...).thenReturn(...)
     ↓
verify(repo).update(...)
```

**Key Deliverables:**
- `UsersServiceImpl.java`
- `UsersServiceImplTest.java`
- `AlreadyAuthenticatedException`

**Takeaway:**
> Never test business logic against real databases; mock external dependencies.

</td>
</tr>
</table>

---

## 🔑 What You Should Learn and Understand

```mermaid
flowchart TD
    A["1️⃣ Difference between Unit Tests & Integration Tests"] --> B["2️⃣ How JUnit 5 Jupiter executes lifecycle hooks (@BeforeEach, @AfterEach)"]
    B --> C["3️⃣ How @ParameterizedTest streams inputs from arrays and CSV files"]
    C --> D["4️⃣ How Spring's EmbeddedDatabaseBuilder spins up an in-memory SQL DB"]
    D --> E["5️⃣ Why test isolation requires resetting state before each test"]
    E --> F["6️⃣ How Mockito creates dynamic bytecode proxies to intercept method calls"]
    F --> G["7️⃣ How to assert method side-effects using verify() and ArgumentCaptor"]

    style A fill:#e1f5fe,stroke:#0288d1,color:#000
    style B fill:#e1f5fe,stroke:#0288d1,color:#000
    style C fill:#e8f5e9,stroke:#388e3c,color:#000
    style D fill:#e8f5e9,stroke:#388e3c,color:#000
    style E fill:#fff3e0,stroke:#f57c00,color:#000
    style F fill:#fce4ec,stroke:#c2185b,color:#000
    style G fill:#f3e5f5,stroke:#7b1fa2,color:#000
```

---

## 💡 Engineering Best Practices

> [!TIP]
> **State Isolation with `@BeforeEach`** — In integration tests (`ex02`), always drop/recreate tables in `@BeforeEach`. If Test A mutates a row that Test B relies on, test execution order will cause intermittent failures (flaky tests).

> [!TIP]
> **Don't Mock What You Don't Own** — Mock your internal interfaces (`UsersRepository`), not third-party libraries or standard Java primitives (`String`, `List`).

> [!TIP]
> **Test Boundaries & Exceptions** — Testing happy paths is trivial. High-quality suites heavily test edge cases: negative numbers, non-existent entity IDs, null pointers, and duplicate credentials.

---

## 📁 Module Directory Structure

```text
Module06/
├── 📄 .gitignore
├── 📄 README.md                               ← you are here
│
├── 🟢 ex00/
│   ├── 📄 README.md
│   └── Tests/
│       ├── pom.xml
│       └── src/
│           ├── main/java/fr/s42/numbers/
│           │   ├── 🔢 NumberWorker.java
│           │   └── ⚠️ IllegalNumberException.java
│           └── test/
│               ├── java/fr/s42/numbers/
│               │   └── 🧪 NumberWorkerTest.java
│               └── resources/
│                   └── 📄 data.csv
│
├── 🟡 ex01/
│   ├── 📄 README.md
│   └── Tests/
│       ├── pom.xml
│       └── src/test/
│           ├── java/fr/s42/repositories/
│           │   └── 🧪 EmbeddedDataSourceTest.java
│           └── resources/
│               ├── 📄 schema.sql
│               └── 📄 data.sql
│
├── 🟠 ex02/
│   ├── 📄 README.md
│   └── Tests/
│       ├── pom.xml
│       └── src/
│           ├── main/java/fr/s42/
│           │   ├── 📦 models/Product.java
│           │   └── 🗄️ repositories/
│           │       ├── 📋 ProductsRepository.java
│           │       └── ⚙️ ProductsRepositoryJdbcImpl.java
│           └── test/java/fr/s42/repositories/
│               └── 🧪 ProductsReposutoryJdbcImplTest.java
│
└── 🔴 ex03/
    ├── 📄 README.md
    └── Tests/
        ├── pom.xml
        └── src/
            ├── main/java/fr/s42/
            │   ├── ⚠️ exceptions/ (AlreadyAuthenticatedException, EntityNotFoundException)
            │   ├── 👤 models/User.java
            │   ├── 🗄️ repositories/UsersRepository.java
            │   └── 🔨 services/
            │       ├── 📋 UsersService.java
            │       └── ⚙️ UsersServiceImpl.java
            └── test/java/fr/s42/services/
                └── 🧪 UsersServiceImplTest.java
```

---

## 🚀 Quick Start

Run the test suite across all four exercises using Maven:

```bash
# Exercise 00 (Parameterized NumberWorker tests)
cd Module06/ex00/Tests && mvn clean test

# Exercise 01 (In-memory HSQLDB datasource validation)
cd Module06/ex01/Tests && mvn clean test

# Exercise 02 (Full CRUD verification against HSQLDB)
cd Module06/ex02/Tests && mvn clean test

# Exercise 03 (Mockito service authentication verification)
cd Module06/ex03/Tests && mvn clean test
```

---

<div align="center">

*Built as part of the 42 Java Curriculum*

![42](https://img.shields.io/badge/42-School-000000?style=for-the-badge&logo=42&logoColor=white)

</div>
