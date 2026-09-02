# Java Module 07 – Reflection & Annotations

## Overview

Java Module 07 explores **metaprogramming** in Java through two foundational technologies: the **Java Reflection API** (`java.lang.reflect`) and **Java Annotations** (both compile-time processing via `javax.annotation.processing` and runtime processing).

Almost every major Java enterprise framework—such as **Spring Framework**, **Hibernate / JPA**, **Jackson**, and **JUnit**—relies on reflection and annotations to inspect classes dynamically, inject dependencies, map database tables, and execute tests without hardcoding class dependencies. This module demystifies that "framework magic" by having you build three progressively sophisticated mini-frameworks from scratch.

---

## Main Programming Concepts Introduced

1. **The Java Reflection API (`java.lang.reflect`)**
   - Inspecting class metadata at runtime: `Class<?>`, `Field`, `Method`, `Constructor`.
   - Discovering declared members via `getDeclaredFields()` and `getDeclaredMethods()`.
   - Bypassing Java access control checks dynamically using `setAccessible(true)`.
   - Instantiating objects dynamically using `clazz.getDeclaredConstructor().newInstance()`.
   - Dynamically invoking methods at runtime using `method.invoke(object, args)`.

2. **Annotation Types & Retention Policies**
   - Creating custom annotations using `@interface`.
   - `RetentionPolicy.SOURCE`: Annotations retained only in source code and discarded by the compiler; processed by compile-time annotation processors.
   - `RetentionPolicy.RUNTIME`: Annotations preserved in bytecode and accessible at runtime through reflection (`element.getAnnotation(...)`).

3. **Compile-Time Annotation Processing (APT)**
   - Extending `javax.annotation.processing.AbstractProcessor`.
   - Hooking into the Java compiler (`javac`) lifecycle to inspect syntax trees (`TypeElement`, `Element`).
   - Using the Java `Filer` API to generate artifacts (such as HTML forms) at build time without runtime overhead.
   - Service Provider Interface (SPI) discovery using Google AutoService (`@AutoService(Processor.class)`).

4. **Object-Relational Mapping (ORM) Engine Design**
   - Bridging domain models and SQL using annotations (`@OrmEntity`, `@OrmColumn`, `@OrmColumnId`).
   - Dynamically generating Data Definition Language (`CREATE TABLE`, `DROP TABLE`) and Data Manipulation Language (`INSERT`, `UPDATE`, `SELECT`) queries at runtime based on entity reflection.

---

## Why These Concepts Are Important

- **Decoupling & Inversion of Control**: Reflection enables programs to instantiate and wire components whose concrete types were not known when the code was compiled.
- **Zero-Boilerplate Code Generation**: Compile-time annotation processing automates tedious tasks (generating DTOs, mappers, HTML templates, serialization logic) during `mvn compile` with zero runtime performance cost.
- **Understanding Enterprise Frameworks**: Frameworks like Spring and Hibernate are no longer black boxes. You understand exactly how `@Entity` maps to a table, how `@Autowired` injects fields, and how frameworks call methods dynamically.

---

## Main Exercises Covered

| Exercise | Name | Domain | Key Concepts & Deliverables |
| :--- | :--- | :--- | :--- |
| **ex00** | Work with Classes | Runtime Reflection | Interactive CLI inspecting `classes.User` and `classes.Car`, dynamic instantiation, field modification, method invocation |
| **ex01** | Annotations - SOURCE | Compile-Time Processing | `@HtmlForm`, `@HtmlInput`, `HtmlProcessor extends AbstractProcessor`, AutoService, HTML generation in `target/classes/` |
| **ex02** | ORM | Runtime ORM Engine | Custom ORM annotations (`@OrmEntity`, `@OrmColumn`, `@OrmColumnId`), `OrmManager` (DDL & DML SQL generator) |

---

## What You Should Learn and Understand

1. How the JVM represents classes in memory as `Class<?>` objects.
2. How to dynamically inspect fields and methods, read parameter types, and handle type casting.
3. How `setAccessible(true)` modifies access checks on private fields and methods.
4. How compile-time annotation processors work during Maven build phases, and how `Filer` outputs generated resources.
5. How an ORM engine inspects class annotations and fields to generate schema DDL (`CREATE TABLE`) and queries (`INSERT`, `UPDATE`, `SELECT`) dynamically.

---

## How Concepts Are Used in My Implementation

- **Interactive CLI with Reflection**: In `ex00`, `app.Program` asks the user for a class name in the `classes` package, lists its fields and methods, prompts for values to create an instance via constructor reflection, updates private fields by name, and executes user-specified methods with parsed arguments.
- **Compiler Hook via AutoService**: In `ex01`, `HtmlProcessor` registers with `javac` using `@AutoService(Processor.class)`. When `mvn clean compile` runs, it scans `@HtmlForm` classes (like `UserForm`), parses `@HtmlInput` field annotations, and generates a formatted HTML form in `target/classes/user_form.html`.
- **Mini ORM Engine**: In `ex02`, `OrmManager` checks `@OrmEntity` and `@OrmColumn` annotations on `models.User` to generate SQL for table creation with appropriate column types (`VARCHAR`, `INT`, `BIGINT`, `BOOLEAN`), `save()` for inserts, `update()` for modifying records by primary key, and `findById()` for dynamic select queries.

---

## Module Directory Structure

```text
Module07/
├── .gitignore
├── README.md
├── ex00/
│   ├── README.md
│   └── Reflection/
│       ├── pom.xml
│       └── src/main/java/
│           ├── app/Program.java
│           └── classes/ (User.java, Car.java)
├── ex01/
│   ├── README.md
│   └── Annotations/
│       ├── pom.xml
│       └── src/main/java/
│           ├── annotations/ (HtmlForm.java, HtmlInput.java)
│           ├── forms/UserForm.java
│           └── processor/HtmlProcessor.java
└── ex02/
    ├── README.md
    └── ORM/
        ├── .gitignore
        ├── pom.xml
        └── src/main/java/
            ├── annotations/ (OrmEntity.java, OrmColumn.java, OrmColumnId.java)
            ├── models/User.java
            ├── manager/OrmManager.java
            └── app/Main.java
```
