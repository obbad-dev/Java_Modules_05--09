# Java Module 05 – SQL / JDBC

## Overview

Java Module 05 introduces relational database management and persistence in Java using **PostgreSQL** and the **Java Database Connectivity (JDBC)** API.

Up to this point in the Piscine, Java applications stored state entirely in-memory using collections (`List`, `Map`, `Set`). In enterprise systems, state must be persistent, ACID-compliant, concurrent, and durable. This module guides you through building a persistent backend for a **Chat application**, covering the full spectrum from relational database schema design to advanced data access patterns.

---

## Main Programming Concepts Introduced

1. **Relational Database Modeling (DDL & DML)**
   - Designing tables with primary keys (`SERIAL PRIMARY KEY`), foreign keys (`REFERENCES`), unique constraints, and junction tables for many-to-many relationships.
   - Populating initial seed data using SQL script files (`schema.sql` and `data.sql`).

2. **Object-Relational Impedance Mismatch**
   - Bridging the gap between tabular relational data (rows, columns, foreign keys) and object-oriented memory graphs (classes, object references, `List<T>`).
   - Redefining domain models (`User`, `Chatroom`, `Message`) with proper `equals()`, `hashCode()`, and `toString()`.

3. **Data Access Object (DAO) / Repository Pattern**
   - Decoupling high-level business logic from low-level database communication details.
   - Using interfaces (`MessagesRepository`, `UsersRepository`) to define persistence contracts.

4. **JDBC (Java Database Connectivity)**
   - Interacting with the database driver using `java.sql.Connection`, `java.sql.PreparedStatement`, `java.sql.ResultSet`, and `java.sql.Timestamp`.
   - Preventing SQL Injection vulnerabilities via parameterized queries.
   - Safely managing database resources using modern `try-with-resources` blocks.

5. **Connection Pooling with HikariCP**
   - Understanding the overhead of creating raw TCP database connections per operation.
   - Using `HikariDataSource` from `com.zaxxer.hikari` for high-performance connection pooling and reuse.

6. **Advanced SQL & Pagination (PostgreSQL CTE)**
   - Implementing single-query pagination (`LIMIT` and `OFFSET`) combined with PostgreSQL Common Table Expressions (`WITH PaginatedUsers AS (...)`).
   - Solving the $N+1$ query problem by fetching entities and their multi-level dependencies in a single round-trip query.

---

## Why These Concepts Are Important

- **Persistence & Durability**: Without a database, all application state disappears when the JVM terminates. Relational databases provide durable, transactional storage.
- **Security**: Using raw string concatenation to construct SQL queries exposes applications to SQL Injection attacks. `PreparedStatement` safely escapes input parameters.
- **Resource Management**: Database connections consume socket descriptors and DBMS worker threads. Leaking connections will crash the database server. Connection pools limit and reuse open connections.
- **Performance**: Executing separate SQL queries for each sub-entity in a loop causes catastrophic latency ($N+1$ queries). Learning to aggregate joined data in a single query is a core backend engineering skill.

---

## Main Exercises Covered

| Exercise | Name | Focus | Key Deliverables |
| :--- | :--- | :--- | :--- |
| **ex00** | Tables and Entities | Relational modeling & Domain Entities | `schema.sql`, `data.sql`, `User`, `Chatroom`, `Message` |
| **ex01** | Read / Find | Repository pattern & HikariCP connection pool | `MessagesRepository`, `MessagesRepositoryJdbcImpl.findById()`, `Program` |
| **ex02** | Create / Save | Data insertion & Generated Keys retrieval | `save(Message)`, custom `NotSavedSubEntityException` |
| **ex03** | Update | Full entity updates & SQL NULL handling | `update(Message)`, handling `null` timestamps via `Types.TIMESTAMP` |
| **ex04** | Find All | Single-query pagination with CTE & Join mapping | `UsersRepository.findAll(int page, int size)` with PostgreSQL CTE |

---

## What You Should Learn and Understand

1. How relational schemas (`One-to-Many`, `Many-to-Many`) translate to Java object references and collections.
2. How to configure and manage a `HikariDataSource` connection pool.
3. How `PreparedStatement` binds parameters and returns auto-generated keys (`Statement.RETURN_GENERATED_KEYS`).
4. How to correctly handle nullable columns and convert between `java.sql.Timestamp` and `java.time.LocalDateTime`.
5. How to write a CTE query in PostgreSQL to paginate parent rows while retrieving 1-to-many and many-to-many child rows, aggregating them in Java using a `LinkedHashMap`.

---

## How Concepts Are Used in My Implementation

- **Data Models**: `User.java`, `Chatroom.java`, and `Message.java` represent chat entities with bidirectionality and overridden `equals()` and `hashCode()` based on primary key IDs.
- **HikariCP**: Integrated across `ex01` to `ex04` using `HikariConfig` and `HikariDataSource` for thread-safe connection pooling.
- **Defensive Error Handling**: Custom runtime exception `NotSavedSubEntityException` guards `save()` against unpersisted sub-entities.
- **Null Safety in SQL**: The `update()` method in `ex03` explicitly checks if `message.getDateTime() == null` and uses `ps.setNull(4, java.sql.Types.TIMESTAMP)`.
- **CTE Optimization**: In `ex04`, `UsersRepositoryJdbcImpl` runs a single `WITH PaginatedUsers AS (...)` query, mapping flat joined result rows into structured `User` objects with their `createdRooms` and `socializedRooms` collections.

---

## Module Directory Structure

```text
Module05/
├── .gitignore
├── README.md
├── ex00/
│   ├── README.md
│   └── Chat/
│       ├── pom.xml
│       └── src/main/
│           ├── java/fr/s42/chat/models/ (User, Chatroom, Message)
│           └── resources/ (schema.sql, data.sql)
├── ex01/
│   ├── README.md
│   └── Chat/
│       ├── pom.xml
│       └── src/main/java/fr/s42/chat/ (app/Program, models, repositories)
├── ex02/
│   ├── README.md
│   └── Chat/
│       ├── pom.xml
│       └── src/main/java/fr/s42/chat/ (exceptions, models, repositories, app)
├── ex03/
│   ├── README.md
│   └── Chat/
│       ├── pom.xml
│       └── src/main/java/fr/s42/chat/ (models, repositories, app)
└── ex04/
    ├── README.md
    └── Chat/
        ├── pom.xml
        └── src/main/java/fr/s42/chat/ (repositories/UsersRepository, etc.)
```
