<div align="center">

# 🐘 Java Module 05 – SQL & JDBC

**Relational Database Modeling, Connection Pooling with HikariCP, DAO Pattern & Advanced CTE Pagination**

![Java](https://img.shields.io/badge/Java-25-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16+-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)
![JDBC](https://img.shields.io/badge/JDBC-4.2-007396?style=for-the-badge&logo=java&logoColor=white)
![HikariCP](https://img.shields.io/badge/HikariCP-Connection_Pool-00C7B7?style=for-the-badge)
![SQL](https://img.shields.io/badge/SQL-Postgres_CTE-F29111?style=for-the-badge&logo=datagrip&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-Build-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)

---

*From In-Memory Collections → Relational Persistence → High-Performance Data Access*

</div>

---

## 📖 Overview

Java Module 05 transitions from temporary in-memory Java state (`List`, `Map`, `Set`) to durable, enterprise-grade relational database persistence using **PostgreSQL** and the standard **Java Database Connectivity (JDBC)** API.

Through building the persistence tier for a real-time **Chat Application**, this module covers the full spectrum of data access engineering: relational schema design, the Object-Relational Impedance Mismatch, thread-safe connection pooling with **HikariCP**, defensive resource management with `try-with-resources`, and solving the notorious $N+1$ query problem using **PostgreSQL Common Table Expressions (CTE)**.

> [!IMPORTANT]
> Database connections are scarce OS resources bound to network sockets and DBMS worker threads. Writing leak-proof, parameterized, and pool-backed persistence code is the foundation of every production backend engineer's skillset.

---

## 🗺️ Module Progression

```mermaid
flowchart LR
    subgraph EX00["🟢 Exercise 00"]
        A["Schema & Models\nschema.sql + data.sql"]
        A1["Domain Entities\nUser, Chatroom, Message"]
        A --> A1
    end

    subgraph EX01["🟡 Exercise 01"]
        B["HikariCP Pool\nHikariDataSource"]
        B1["MessagesRepository\nfindById(id)"]
        B --> B1
    end

    subgraph EX02["🟠 Exercise 02"]
        C["INSERT & Auto-Keys\nRETURN_GENERATED_KEYS"]
        C1["Defensive Validation\nNotSavedSubEntityException"]
        C --> C1
    end

    subgraph EX03["🔵 Exercise 03"]
        D["UPDATE Operations\nFull Field Replacement"]
        D1["SQL NULL Safety\nTypes.TIMESTAMP"]
        D --> D1
    end

    subgraph EX04["🔴 Exercise 04"]
        E["Advanced CTE Query\nWITH PaginatedUsers"]
        E1["Single-Trip Join\nZero N+1 Overhead"]
        E --> E1
    end

    EX00 ==>|"Read via Pool"| EX01 ==>|"Write Operations"| EX02 ==>|"Mutate Records"| EX03 ==>|"Scalable Read"| EX04

    style EX00 fill:#d4edda,stroke:#28a745,color:#000
    style EX01 fill:#fff3cd,stroke:#ffc107,color:#000
    style EX02 fill:#ffe5d0,stroke:#fd7e14,color:#000
    style EX03 fill:#d1ecf1,stroke:#17a2b8,color:#000
    style EX04 fill:#f8d7da,stroke:#dc3545,color:#000
```

---

## 🧠 Core Architecture & Design Patterns

### 🏛️ Data Access Object (DAO) / Repository Pattern

```mermaid
flowchart TD
    subgraph APP["Application Layer"]
        CLI["Program.java / Main"]
    end

    subgraph REPO["Data Access Layer"]
        IFACE["«interface»\nUsersRepository / MessagesRepository"]
        IMPL["UsersRepositoryJdbcImpl\nMessagesRepositoryJdbcImpl"]
        IFACE -.->|implements| IMPL
    end

    subgraph POOL["Connection Layer"]
        HIKARI["HikariDataSource\n(Thread-Safe Connection Pool)"]
    end

    subgraph DB["Database Engine"]
        PG[("PostgreSQL\n(users, chatrooms, messages)")]
    end

    CLI -->|"calls interface"| IFACE
    IMPL -->|"getConnection()"| HIKARI
    HIKARI -->|"pooled TCP sockets"| PG

    style APP fill:#e3f2fd,stroke:#1565c0,color:#000
    style REPO fill:#e8f5e9,stroke:#2e7d32,color:#000
    style POOL fill:#fff3e0,stroke:#e65100,color:#000
    style DB fill:#ede7f6,stroke:#4527a0,color:#000
```

---

### ⚡ The $N+1$ Query Problem vs CTE Solution (ex04)

```mermaid
sequenceDiagram
    autonumber
    actor App as Java Application
    participant DB as PostgreSQL Database

    Note over App, DB: ❌ The Naive N+1 Anti-Pattern (Slow & Network-Heavy)
    App->>DB: SELECT * FROM users LIMIT 10 OFFSET 0;
    loop For each of 10 Users
        App->>DB: SELECT * FROM chatrooms WHERE owner_id = ?
        App->>DB: SELECT * FROM users_chatrooms WHERE user_id = ?
    end
    Note over App, DB: Total: 1 + 10 + 10 = 21 database round-trips!

    Note over App, DB: ✅ The CTE Single-Trip Pattern (ex04 Implementation)
    App->>DB: WITH PaginatedUsers AS (SELECT * FROM users LIMIT ? OFFSET ?)<br/>SELECT * FROM PaginatedUsers LEFT JOIN chatrooms... LEFT JOIN users_chatrooms...
    DB-->>App: Single aggregated tabular result set
    Note over App, DB: Total: Exactly 1 database round-trip! Java maps rows via LinkedHashMap.
```

---

## 📋 Concepts Breakdown by Exercise

| Capability / Concept | ex00 | ex01 | ex02 | ex03 | ex04 |
| :--- | :---: | :---: | :---: | :---: | :---: |
| DDL & DML Scripts (`schema.sql`, `data.sql`) | ✅ | ✅ | ✅ | ✅ | ✅ |
| Domain Models (`User`, `Chatroom`, `Message`) | ✅ | ✅ | ✅ | ✅ | ✅ |
| `HikariDataSource` Connection Pooling | | ✅ | ✅ | ✅ | ✅ |
| `PreparedStatement` Parameter Binding | | ✅ | ✅ | ✅ | ✅ |
| `ResultSet` to Domain Entity Mapping | | ✅ | ✅ | ✅ | ✅ |
| Generated Keys Retrieval (`RETURN_GENERATED_KEYS`) | | | ✅ | | |
| Custom Validation Exception (`NotSavedSubEntityException`) | | | ✅ | | |
| Handling SQL `NULL` with `Types.TIMESTAMP` | | | | ✅ | |
| PostgreSQL CTE (`WITH PaginatedUsers AS ...`) | | | | | ✅ |
| In-Memory Entity Aggregation (`LinkedHashMap`) | | | | | ✅ |

---

## 🎯 Exercises Overview

<table>
<tr>
<td width="20%" valign="top">

### 🟢 ex00
**Tables and Entities**

```text
schema.sql
    ↓
PostgreSQL Tables
    ↓
Java Domain Models
(equals/hashCode by ID)
```

**Key Files:**
- `schema.sql`
- `data.sql`
- `User.java`
- `Chatroom.java`
- `Message.java`

**Takeaway:**
> Relational foreign keys translate to Java object references and collections.

</td>
<td width="20%" valign="top">

### 🟡 ex01
**Read / Find**

```text
HikariDataSource
    ↓
Connection from Pool
    ↓
SELECT by ID
    ↓
Optional<Message>
```

**Key Files:**
- `MessagesRepository`
- `MessagesRepositoryJdbcImpl`
- `Program.java`

**Takeaway:**
> Always return `Optional<T>` for queries that can yield zero rows.

</td>
<td width="20%" valign="top">

### 🟠 ex02
**Create / Save**

```text
Validate Sub-Entities
    ↓ (Pass)
INSERT Statement
    ↓
Retrieve Generated ID
    ↓
entity.setId(newId)
```

**Key Files:**
- `save(Message)`
- `NotSavedSubEntityException`

**Takeaway:**
> Don't persist child records if author or room does not exist in DB.

</td>
<td width="20%" valign="top">

### 🔵 ex03
**Update**

```text
Existing Entity in DB
    ↓
update(Message)
    ↓
Handle NULL timestamp
    ↓
Database row mutated
```

**Key Files:**
- `update(Message)`
- `Types.TIMESTAMP`

**Takeaway:**
> Must set `ps.setNull()` explicitly when object fields are `null`.

</td>
<td width="20%" valign="top">

### 🔴 ex04
**Find All with CTE**

```text
WITH PaginatedUsers AS (
  SELECT * FROM users
  LIMIT ? OFFSET ?
)
LEFT JOIN rooms...
    ↓
LinkedHashMap Aggregation
```

**Key Files:**
- `findAll(page, size)`

**Takeaway:**
> Single CTE query eliminates the $N+1$ query performance trap.

</td>
</tr>
</table>

---

## 🔑 What You Should Learn and Understand

```mermaid
flowchart TD
    A["1️⃣ How relational constraints (PK, FK, Junction) map to OOP"] --> B["2️⃣ How connection pools reduce TCP socket allocation overhead"]
    B --> C["3️⃣ How PreparedStatement eliminates SQL Injection vulnerabilities"]
    C --> D["4️⃣ How to retrieve database-generated identity keys safely"]
    D --> E["5️⃣ How to bind NULL parameters without throwing NullPointerExceptions"]
    E --> F["6️⃣ How to construct PostgreSQL Common Table Expressions (CTE)"]
    F --> G["7️⃣ How to fold flat multi-table result rows into nested object graphs"]

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
> **Try-With-Resources Everywhere** — Always wrap `Connection`, `PreparedStatement`, and `ResultSet` in `try (...)` blocks. Omitting this causes socket leaks that will eventually lock the PostgreSQL server.

> [!TIP]
> **Never Concatenate Strings in SQL** — Always use `PreparedStatement` with `?` parameter placeholders. String concatenation like `"WHERE id = " + id` is an immediate security vulnerability.

> [!TIP]
> **HikariCP Sizing** — Never set pool size arbitrarily high. The optimal pool size follows the formula: `connections = ((core_count * 2) + effective_spindle_count)`.

---

## 📁 Module Directory Structure

```text
Module05/
├── 📄 .gitignore
├── 📄 README.md                               ← you are here
│
├── 🟢 ex00/
│   ├── 📄 README.md
│   └── Chat/
│       ├── pom.xml
│       └── src/main/
│           ├── java/fr/s42/chat/models/
│           │   ├── 👤 User.java
│           │   ├── 💬 Chatroom.java
│           │   └── ✉️ Message.java
│           └── resources/
│               ├── 📄 schema.sql
│               └── 📄 data.sql
│
├── 🟡 ex01/
│   ├── 📄 README.md
│   └── Chat/
│       ├── pom.xml
│       └── src/main/java/fr/s42/chat/
│           ├── 🚀 app/Program.java
│           ├── 📦 models/ (User, Chatroom, Message)
│           └── 🗄️ repositories/
│               ├── 📋 MessagesRepository.java
│               └── ⚙️ MessagesRepositoryJdbcImpl.java
│
├── 🟠 ex02/
│   ├── 📄 README.md
│   └── Chat/
│       ├── pom.xml
│       └── src/main/java/fr/s42/chat/
│           ├── 🚀 app/Program.java
│           ├── ⚠️ exceptions/NotSavedSubEntityException.java
│           ├── 📦 models/ (User, Chatroom, Message)
│           └── 🗄️ repositories/MessagesRepositoryJdbcImpl.java
│
├── 🔵 ex03/
│   ├── 📄 README.md
│   └── Chat/
│       ├── pom.xml
│       └── src/main/java/fr/s42/chat/
│           ├── 🚀 app/Program.java
│           ├── 📦 models/ (User, Chatroom, Message)
│           └── 🗄️ repositories/MessagesRepositoryJdbcImpl.java
│
└── 🔴 ex04/
    ├── 📄 README.md
    └── Chat/
        ├── pom.xml
        └── src/main/java/fr/s42/chat/
            ├── 🚀 app/Program.java
            ├── 📦 models/ (User, Chatroom, Message)
            └── 🗄️ repositories/
                ├── 📋 UsersRepository.java
                └── ⚙️ UsersRepositoryJdbcImpl.java
```

---

## 🚀 Quick Start

Ensure a local PostgreSQL instance is running on port `5432` with a database named `chat_db`:

```bash
# Initialize database schema and seed data
psql -U postgres -d chat_db -f Module05/ex00/Chat/src/main/resources/schema.sql
psql -U postgres -d chat_db -f Module05/ex00/Chat/src/main/resources/data.sql

# Exercise 01 (Find by ID)
cd Module05/ex01/Chat && mvn clean compile exec:java

# Exercise 02 (Save new Message)
cd Module05/ex02/Chat && mvn clean compile exec:java

# Exercise 03 (Update Message)
cd Module05/ex03/Chat && mvn clean compile exec:java

# Exercise 04 (CTE Paginated Find All)
cd Module05/ex04/Chat && mvn clean compile exec:java
```

---

<div align="center">

*Built as part of the 42 Java Curriculum*

![42](https://img.shields.io/badge/42-School-000000?style=for-the-badge&logo=42&logoColor=white)

</div>
