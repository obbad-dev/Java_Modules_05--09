<div align="center">

# ☘️ Java Module 08 – Spring Framework

**Enterprise-level Java development with IoC, Dependency Injection & Annotation-based Configuration**

![Spring](https://img.shields.io/badge/Spring_Framework-6.2.x-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![Java](https://img.shields.io/badge/Java-25-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-5432-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)
![HikariCP](https://img.shields.io/badge/HikariCP-Connection_Pool-00C7B7?style=for-the-badge)
![JUnit5](https://img.shields.io/badge/JUnit_5-Testing-25A162?style=for-the-badge&logo=junit5&logoColor=white)
![H2](https://img.shields.io/badge/H2-In--Memory_DB-0000BB?style=for-the-badge)
![Maven](https://img.shields.io/badge/Maven-Build-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)

---

*From XML wiring → JdbcTemplate → Pure Annotation Config → Integration Testing*

</div>

---

## 📖 Overview

Module 08 introduces **enterprise-level Java development** using the **Spring Framework**. The module progressively builds understanding of Spring's **Inversion of Control (IoC)** container and **Dependency Injection (DI)** mechanisms — starting with XML-based configuration, advancing through database connectivity with `JdbcTemplate`, and culminating in a full migration to **annotation-based configuration** with integration testing.

> [!IMPORTANT]
> Spring Framework is the foundation of nearly all modern Java backend systems (Spring Boot, Spring Cloud, Spring Security). This module teaches how the IoC container manages object lifecycles, resolves dependencies, and decouples components — the same principles that power every production Spring application.

---

## 🗺️ Module Progression

```mermaid
flowchart LR
    subgraph EX00["🟢 Exercise 00"]
        A["XML Configuration\ncontext.xml"]
        A1["ClassPathXml\nApplicationContext"]
        A --> A1
    end

    subgraph EX01["🟡 Exercise 01"]
        B["DataSource Beans\nHikari + DriverManager"]
        B1["JdbcTemplate\n+ Raw JDBC"]
        B2["Property\nPlaceholders"]
        B --> B1
        B2 --> B
    end

    subgraph EX02["🔴 Exercise 02"]
        C["@Configuration\n@Bean"]
        C1["@Repository\n@Service"]
        C2["@Autowired\n@Qualifier"]
        C3["@SpringJUnitConfig\nH2 In-Memory"]
        C --> C1 --> C2 --> C3
    end

    EX00 ==>|"Add DB Layer"| EX01 ==>|"Kill XML"| EX02

    style EX00 fill:#d4edda,stroke:#28a745,color:#000
    style EX01 fill:#fff3cd,stroke:#ffc107,color:#000
    style EX02 fill:#f8d7da,stroke:#dc3545,color:#000
```

---

## 🧠 Core Concepts Introduced

### 🔄 Inversion of Control & Dependency Injection

```mermaid
flowchart TD
    subgraph BEFORE["❌ Without Spring"]
        M1["Main.java"] -->|"new"| P1["PreProcessor"]
        M1 -->|"new"| R1["Renderer(preProcessor)"]
        M1 -->|"new"| PR1["Printer(renderer)"]
    end

    subgraph AFTER["✅ With Spring IoC"]
        CTX["Spring Container"] -->|"creates & injects"| P2["PreProcessor"]
        CTX -->|"creates & injects"| R2["Renderer"]
        CTX -->|"creates & injects"| PR2["Printer"]
        M2["Main.java"] -->|"getBean()"| CTX
    end

    style BEFORE fill:#ffcccc,stroke:#cc0000,color:#000
    style AFTER fill:#ccffcc,stroke:#00cc00,color:#000
```

### 📋 Concepts by Exercise

| Concept | ex00 | ex01 | ex02 |
| :--- | :---: | :---: | :---: |
| IoC Container & Bean Lifecycle | ✅ | ✅ | ✅ |
| XML `<bean>` Configuration | ✅ | ✅ | ❌ |
| Constructor & Setter Injection | ✅ | ✅ | ✅ |
| `DataSource` (DriverManager + Hikari) | | ✅ | ✅ |
| Raw JDBC vs `JdbcTemplate` | | ✅ | ✅ |
| Property Placeholders (`db.properties`) | | ✅ | ✅ |
| `@Configuration` + `@Bean` | | | ✅ |
| `@Repository` / `@Service` Stereotypes | | | ✅ |
| `@ComponentScan` (ASM bytecode scanning) | | | ✅ |
| `@PropertySource` + `@Value` | | | ✅ |
| `@Autowired` + `@Qualifier` | | | ✅ |
| Integration Testing (H2 + JUnit 5) | | | ✅ |

---

## 🏗️ Architecture Evolution

### ex00 — XML Wiring
```mermaid
graph LR
    XML["📄 context.xml"] -->|defines| B1["PreProcessor Bean"]
    XML -->|defines| B2["Renderer Bean"]
    XML -->|defines| B3["Printer Bean"]
    B1 -->|injected into| B2
    B2 -->|injected into| B3

    style XML fill:#f9f,stroke:#333,color:#000
```

### ex01 — Database Layer Added
```mermaid
graph TD
    XML["📄 context.xml"] -->|property-placeholder| PROPS["📄 db.properties"]
    XML -->|defines| DS1["DriverManagerDataSource"]
    XML -->|defines| DS2["HikariDataSource"]
    DS1 -->|injected into| R1["UsersRepositoryJdbcImpl\n(raw PreparedStatement)"]
    DS2 -->|injected into| R2["UsersRepositoryJdbcTemplateImpl\n(NamedParameterJdbcTemplate)"]

    style XML fill:#f9f,stroke:#333,color:#000
    style PROPS fill:#ffd,stroke:#333,color:#000
```

### ex02 — Pure Annotations (No XML)
```mermaid
graph TD
    CONFIG["☕ ApplicationConfig.java\n@Configuration\n@PropertySource\n@ComponentScan"] -->|"@Bean"| DS1["DataSource\nhikariDataSource"]
    CONFIG -->|"@Bean"| DS2["DataSource\ndriverManagerDataSource"]
    CONFIG -->|"scans"| REPO1["@Repository\nUsersRepositoryJdbcImpl"]
    CONFIG -->|"scans"| REPO2["@Repository\nUsersRepositoryJdbcTemplateImpl"]
    CONFIG -->|"scans"| SVC["@Service\nUsersServiceImpl"]

    DS2 -->|"@Qualifier"| REPO1
    DS1 -->|"@Qualifier"| REPO2
    REPO2 -->|"@Qualifier"| SVC

    TCONFIG["🧪 TestApplicationConfig\n@Configuration\n@Import"] -->|"@Bean"| TDS["EmbeddedDatabase\nH2 MODE=PostgreSQL"]
    TCONFIG -->|"@Import"| TREPO["UsersRepositoryJdbcTemplateImpl"]
    TCONFIG -->|"@Import"| TSVC["UsersServiceImpl"]
    TDS --> TREPO
    TREPO --> TSVC

    style CONFIG fill:#d4edda,stroke:#28a745,color:#000
    style TCONFIG fill:#cce5ff,stroke:#007bff,color:#000
```

---

## 🎯 Exercises Overview

<table>
<tr>
<td width="33%" valign="top">

### 🟢 ex00 — Spring Context
**XML-based IoC Configuration**

```text
PreProcessor
     ↓ (injected)
  Renderer
     ↓ (injected)
   Printer
     ↓
 "PREFIX HELLO"
```

**Key Files:**
- `context.xml`
- `Main.java`
- 6 interface/impl classes

**Key Takeaway:**
> Components declare *what* they need. The container decides *how* to wire them.

</td>
<td width="33%" valign="top">

### 🟡 ex01 — JdbcTemplate
**Database Connectivity via XML**

```text
db.properties
     ↓ (${db.url})
  context.xml
     ↓
 DataSource ×2
     ↓
Repository ×2
     ↓
  CRUD Ops
```

**Key Files:**
- `context.xml` + `db.properties`
- `UsersRepositoryJdbcImpl`
- `UsersRepositoryJdbcTemplateImpl`

**Key Takeaway:**
> JdbcTemplate eliminates ~15 lines of boilerplate per query.

</td>
<td width="33%" valign="top">

### 🔴 ex02 — AnnotationConfig
**Full Migration + Testing**

```text
 context.xml  →  🗑️ DELETED
       ↓
@Configuration
@ComponentScan
@Autowired
@Qualifier
       ↓
  @SpringJUnitConfig
  EmbeddedDatabase (H2)
```

**Key Files:**
- `ApplicationConfig.java`
- `TestApplicationConfig.java`
- `UsersServiceImplTest.java`

**Key Takeaway:**
> Zero XML. Annotations ARE the configuration.

</td>
</tr>
</table>

---

## 🔑 What You Should Learn and Understand

```mermaid
flowchart TD
    A["1️⃣ How Spring IoC creates\nand manages bean lifecycles"] --> B["2️⃣ How XML parses into\nBeanDefinition objects"]
    B --> C["3️⃣ How @Configuration uses\nCGLIB proxying for singletons"]
    C --> D["4️⃣ How ClassPathBeanDefinitionScanner\nuses ASM bytecode analysis"]
    D --> E["5️⃣ How @Autowired resolves by type\n& @Qualifier resolves by name"]
    E --> F["6️⃣ How EmbeddedDatabaseBuilder\ncreates in-memory test DBs"]
    F --> G["7️⃣ The full progression:\nXML → @Bean → @Component → @ComponentScan"]

    style A fill:#e1f5fe,stroke:#0288d1,color:#000
    style B fill:#e1f5fe,stroke:#0288d1,color:#000
    style C fill:#e8f5e9,stroke:#388e3c,color:#000
    style D fill:#e8f5e9,stroke:#388e3c,color:#000
    style E fill:#fff3e0,stroke:#f57c00,color:#000
    style F fill:#fff3e0,stroke:#f57c00,color:#000
    style G fill:#fce4ec,stroke:#c62828,color:#000
```

---

## 💡 Why These Concepts Matter

> [!TIP]
> **Loose Coupling** — IoC/DI eliminates hard-coded `new` calls. Components declare *what* they need, not *how* to get it — making code testable, maintainable, and swappable.

> [!TIP]
> **Configuration Evolution** — Understanding XML → annotations → component scanning explains *why* modern Spring Boot apps need almost zero configuration.

> [!TIP]
> **Database Best Practices** — `JdbcTemplate` prevents the most common JDBC bugs (unclosed connections, uncaught SQLExceptions). HikariCP provides production-grade connection pooling.

> [!TIP]
> **Testability** — In-memory databases + separate test configs enable fast, reliable integration tests with zero external infrastructure dependency.

---

## 📁 Module Directory Structure

```text
Module08/
├── 📄 Module08.subject.pdf
├── 📄 README.md                          ← you are here
│
├── 🟢 ex00/
│   ├── 📄 README.md
│   └── Spring/
│       ├── pom.xml
│       └── src/main/
│           ├── java/rabat/s1337/spring/
│           │   ├── 🚀 app/Main.java
│           │   ├── 🔧 preProcessor/ (PreProcessor, ToUpperImpl, ToLowerImpl)
│           │   ├── 🖨️ printer/ (Printer, WithDateTimeImpl, WithPrefixImpl)
│           │   └── 📺 render/ (Render, ErrImpl, StandardImpl)
│           └── resources/
│               └── 📄 context.xml
│
├── 🟡 ex01/
│   ├── 📄 README.md
│   └── Service/
│       ├── pom.xml
│       └── src/main/
│           ├── java/_42/spring/service/
│           │   ├── 🚀 application/Main.java
│           │   ├── 📦 models/User.java
│           │   └── 🗄️ repositories/ (CrudRepository, UsersRepository,
│           │       UsersRepositoryJdbcImpl, UsersRepositoryJdbcTemplateImpl)
│           └── resources/
│               ├── 📄 context.xml
│               ├── 📄 db.properties
│               ├── 📄 schema.sql
│               └── 📄 data.sql
│
└── 🔴 ex02/
    ├── 📄 README.md
    └── Service/
        ├── pom.xml
        └── src/
            ├── main/java/_42/spring/service/
            │   ├── 🚀 application/Main.java
            │   ├── ⚙️ config/ApplicationConfig.java
            │   ├── 📦 models/User.java
            │   ├── 🗄️ repositories/ (CrudRepository, UsersRepository,
            │   │   UsersRepositoryJdbcImpl, UsersRepositoryJdbcTemplateImpl)
            │   └── 🔨 services/ (UsersService, UsersServiceImpl)
            ├── main/resources/
            │   ├── 📄 db.properties
            │   ├── 📄 schema.sql
            │   └── 📄 data.sql
            └── test/java/_42/spring/service/
                ├── 🧪 config/TestApplicationConfig.java
                └── 🧪 services/UsersServiceImplTest.java
```

---

## 🚀 Quick Start

```bash
# Exercise 00
cd Module08/ex00/Spring && mvn clean compile exec:java

# Exercise 01 (requires running PostgreSQL)
cd Module08/ex01/Service && mvn clean compile exec:java

# Exercise 02 — Run app (requires running PostgreSQL)
cd Module08/ex02/Service && mvn clean compile exec:java

# Exercise 02 — Run tests (no external DB needed)
cd Module08/ex02/Service && mvn clean test
```

---

## 🧩 The XML → Annotation Migration at a Glance

| What changed | XML (`context.xml`) | Annotations |
| :--- | :--- | :--- |
| Configuration source | `<beans>` root element | `@Configuration` class |
| Bean definition | `<bean id="..." class="...">` | `@Bean` method or `@Component` / `@Repository` / `@Service` |
| Constructor injection | `<constructor-arg ref="..."/>` | `@Autowired` on constructor |
| Disambiguation | `ref="specificBean"` | `@Qualifier("specificBean")` |
| Property loading | `<context:property-placeholder>` | `@PropertySource` + `@Value` |
| Component scanning | `<context:component-scan>` | `@ComponentScan` |
| Context bootstrap | `ClassPathXmlApplicationContext` | `AnnotationConfigApplicationContext` |

---

<div align="center">

*Built as part of the 42 Java Curriculum*

![42](https://img.shields.io/badge/42-School-000000?style=for-the-badge&logo=42&logoColor=white)

</div>
